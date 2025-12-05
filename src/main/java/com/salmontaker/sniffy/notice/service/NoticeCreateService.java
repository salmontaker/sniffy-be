package com.salmontaker.sniffy.notice.service;

import com.salmontaker.sniffy.founditem.domain.FoundItem;
import com.salmontaker.sniffy.founditem.repository.FoundItemRepository;
import com.salmontaker.sniffy.notice.domain.Notice;
import com.salmontaker.sniffy.notice.repository.NoticeRepository;
import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.domain.UserKeyword;
import com.salmontaker.sniffy.user.repository.UserKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeCreateService {
    private final UserKeywordRepository userKeywordRepository;
    private final FoundItemRepository foundItemRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public void createKeywordNotification() {
        Map<User, List<UserKeyword>> keywordsMap = getUserKeywordMap();
        List<FoundItem> todayItems = findTodayItems();
        List<Notice> notices = new ArrayList<>();

        for (Map.Entry<User, List<UserKeyword>> entry : keywordsMap.entrySet()) {
            User user = entry.getKey();
            List<UserKeyword> userKeywords = entry.getValue();

            for (UserKeyword keyword : userKeywords) {
                List<FoundItem> matchedItems = findMatchedItems(todayItems, keyword.getKeyword());
                if (matchedItems.isEmpty()) {
                    continue;
                }

                notices.add(createNotification(user, keyword.getKeyword(), matchedItems));
            }
        }

        noticeRepository.saveAll(notices);
    }

    private Map<User, List<UserKeyword>> getUserKeywordMap() {
        return userKeywordRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(UserKeyword::getUser));
    }

    private List<FoundItem> findTodayItems() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        return foundItemRepository.findToday(startOfToday);
    }

    private List<FoundItem> findMatchedItems(List<FoundItem> items, String keyword) {
        String lowerKeyword = keyword.toLowerCase();

        return items.stream()
                .filter(item -> item.getFdPrdtNm().toLowerCase().contains(lowerKeyword))
                .toList();
    }

    private Notice createNotification(User user, String keyword, List<FoundItem> matched) {
        String title = String.format("[키워드 알림] \"%s\"에 해당하는 물품 수: %d", keyword, matched.size());
        String content = matched.stream()
                .limit(5)
                .map(this::summaryItem)
                .collect(Collectors.joining("\n"));

        return Notice.create(user, title, content);
    }

    private String summaryItem(FoundItem item) {
        String url = String.format("https://www.lost112.go.kr/find/findDetail.do?ATC_ID=%s&FD_SN=%d", item.getAtcId(), item.getFdSn());
        return String.format("%s(%s): %s", item.getAgency().getName(), item.getFdPrdtNm(), url);
    }
}
