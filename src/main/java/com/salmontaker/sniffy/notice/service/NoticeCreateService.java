package com.salmontaker.sniffy.notice.service;

import com.salmontaker.sniffy.founditem.domain.FoundItem;
import com.salmontaker.sniffy.founditem.repository.FoundItemRepository;
import com.salmontaker.sniffy.notice.domain.Notice;
import com.salmontaker.sniffy.notice.repository.NoticeRepository;
import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.domain.UserKeyword;
import com.salmontaker.sniffy.user.domain.UserPreference;
import com.salmontaker.sniffy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeCreateService {
    private final UserRepository userRepository;
    private final FoundItemRepository foundItemRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public void createKeywordNotification() {
        List<User> targetUsers = userRepository.findAllWithKeywordAndPushEnabled();
        List<FoundItem> todayItems = findTodayItems();
        List<Notice> notices = new ArrayList<>();

        for (User user : targetUsers) {
            notices.addAll(processUserNotice(user, todayItems));
        }

        noticeRepository.saveAll(notices);
    }

    private List<Notice> processUserNotice(User user, List<FoundItem> todayItems) {
        List<Notice> userNotices = new ArrayList<>();
        UserPreference preference = user.getUserPreference();

        // 즐겨찾기한 센터 우선 설정이 켜져있을 경우에만 센터 ID 추출
        Set<Integer> favoriteAgencyIds = getFavoriteAgencyIds(user, preference);

        for (UserKeyword keyword : user.getKeywords()) {
            List<FoundItem> matchedItems = getMatchedItems(todayItems, keyword.getKeyword());

            if (!matchedItems.isEmpty()) {
                List<FoundItem> sortedItems = sortItems(matchedItems, favoriteAgencyIds);
                userNotices.add(createNotice(user, keyword.getKeyword(), sortedItems));
            }
        }
        return userNotices;
    }

    private Set<Integer> getFavoriteAgencyIds(User user, UserPreference preference) {
        if (preference == null || preference.getIsFavoriteFirst()) {
            return Set.of();
        }

        // favorites 컬렉션 접근 시 지연 로딩 발생 (batch 사이즈로 최적화)
        return user.getFavorites().stream()
                .map(fav -> fav.getAgency().getId())
                .collect(Collectors.toSet());
    }

    private List<FoundItem> getMatchedItems(List<FoundItem> items, String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return items.stream()
                .filter(item -> item.getFdPrdtNm().toLowerCase().contains(lowerKeyword))
                .toList();
    }

    private List<FoundItem> sortItems(List<FoundItem> items, Set<Integer> favoriteAgencyIds) {
        if (favoriteAgencyIds.isEmpty()) {
            return items;
        }

        // 관심 센터 물품을 리스트 앞쪽으로 정렬
        items.sort((item1, item2) -> {
            boolean isFav1 = favoriteAgencyIds.contains(item1.getAgency().getId());
            boolean isFav2 = favoriteAgencyIds.contains(item2.getAgency().getId());

            return Boolean.compare(isFav2, isFav1);
        });

        return items;
    }

    private Notice createNotice(User user, String keyword, List<FoundItem> items) {
        String title = String.format("\"%s\"에 해당하는 물품이 %d개 등록되었어요!", keyword, items.size());

        String content = items.stream()
                .limit(5)
                .map(this::summaryItem)
                .collect(Collectors.joining("\n"));

        return Notice.create(user, title, content);
    }

    private String summaryItem(FoundItem item) {
        String url = String.format("https://www.lost112.go.kr/find/findDetail.do?ATC_ID=%s&FD_SN=%d", item.getAtcId(), item.getFdSn());
        return String.format("%s(%s): %s", item.getAgency().getName(), item.getFdPrdtNm(), url);
    }

    private List<FoundItem> findTodayItems() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        return foundItemRepository.findToday(startOfToday);
    }
}