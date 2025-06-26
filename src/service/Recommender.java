package service;

import model.Food;
import model.MealLog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Recommender {

    /** LIST 추천
     * 좋아요 + 최근 3일 먹은 음식 제외하고 1개 추천
     * @param allFoods 전체 음식 목록
     * @param allLogs 전체 식사 기록 목록
     * @return 추천 음식 1개 (없으면 null)
     */
    public Food recommendBaseLikedAnd3Day(List<Food> allFoods, List<MealLog> allLogs) {

        // 1. 최근 3일간 먹은 음식 ID 목록 만들기
        List<Integer> recentFoodIds = new ArrayList<>();
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);

        for (int i = 0; i < allLogs.size(); i++) {
            MealLog log = allLogs.get(i);
            if (log.getEatenDate().isAfter(threeDaysAgo) || log.getEatenDate().isEqual(threeDaysAgo)) {
                if (!recentFoodIds.contains(log.getFoodId())) {
                    recentFoodIds.add(log.getFoodId());
                }
            }
        }

        // 2. 조건을 만족하는 음식들을 후보 리스트에 추가
        List<Food> candidates = new ArrayList<>();

        for (int i = 0; i < allFoods.size(); i++) {
            Food food = allFoods.get(i);
            boolean isLiked = food.getPreference().isLiked();
            boolean isRecentlyEaten = recentFoodIds.contains(food.getId());

            if (isLiked && !isRecentlyEaten) {
                candidates.add(food);
            }
        }

        // 3. 후보 리스트가 비어 있지 않으면 랜덤으로 하나 반환
        if (!candidates.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(candidates.size()); // 0 이상 ~ size 미만
            return candidates.get(index);
        }

        // 추천할 음식이 없을 경우 null 반환
        return null;

    }
    /** SET 추천
     * 중복 회피
     * 사용자가 "싫어요" 표시한 음식 ID를 Set으로 구성
     * 사용자가 싫어요 표시한 음식은 빠르게 제외하고 추천
     */
    public Food recommendBySet(){
        return null;
    }

    /** Map 추천
     * key값 별 추천
     * 카테고리별 섭취 횟수 Map 생성 많이 먹은 카테고리는 제외 → 편중 방지
     */
    public Food recommendByMap(){
        return null;
    }

    /** Queue 추천
     * 선입선출
     * 섭취 기록이 적은 음식일수록 우선순위 높게 설정
     * PriorityQueue로 빈도 기반 정렬
     * 선입선출 로직 적용
     */
    public Food recommendByQueue(){
        return null;
    }

    /** STACK 추천
     * 후입선출
     * 최근 먹은 음식을 Stack에 쌓고,
     * 가장 마지막에 먹은 음식부터 하나씩 꺼내며
     * 관련 추천을 하거나 중복 섭취를 피할 수 있도록 안내하는 로직.
     */
    public Food recommendByStack(){
        return null;
    }


}
