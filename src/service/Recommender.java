package service;

import model.Food;
import model.MealLog;

import java.time.LocalDate;
import java.util.*;

public class Recommender {

    /** LIST 추천
     * 최근 2일 먹은 음식 제외하고 1개 랜덤 추천
     * @return 추천 음식 1개 (없으면 null)
     */
    public Food recommendBaseLikedAnd3Day(List<Food> allFoods, List<MealLog> allLogs) {

        // 1. 최근 2일간 먹은 음식 ID 목록 만들기
        List<Integer> recentFoodIds = new ArrayList<>();
        LocalDate threeDaysAgo = LocalDate.now().minusDays(2);

        for (int i = 0; i < allLogs.size(); i++) {
            MealLog log = allLogs.get(i);
            if (log.getEatenDate().isAfter(threeDaysAgo) || log.getEatenDate().isEqual(threeDaysAgo)) {
                if (!recentFoodIds.contains(log.getFoodId())) {
                    recentFoodIds.add(log.getFoodId());
                }
            }
        }

        // 2. 최근 2일간 먹지 않은 음식 후보 수집
        List<Food> candidates = new ArrayList<>();

        for (int i = 0; i < allFoods.size(); i++) {
            Food food = allFoods.get(i);
            boolean isRecentlyEaten = recentFoodIds.contains(food.getId());

            if (!isRecentlyEaten) {
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
    public Food recommendBySet(List<Food> allFoods){
        Set<Integer> dislikedIds = new HashSet<>();

        for (Food food : allFoods) {
            if (!food.getPreference().isLiked()) {
                dislikedIds.add(food.getId());
            }
        }

        // 싫어요 아닌 음식 후보 추출
        List<Food> candidates = new ArrayList<>();
        for (Food food : allFoods) {
            if (!dislikedIds.contains(food.getId())) {
                candidates.add(food);
            }
        }

        if (!candidates.isEmpty()) {
            return candidates.get(new Random().nextInt(candidates.size()));
        }
        return null;

    }

    /** Map 추천
     * key값 별 추천
     * 싫어요가 많은 카테고리 음식 추천
     * 편식방지
     */
    public Food recommendByMap(List<Food> allFoods) {
        Map<Food.Category, List<Food>> categoryMap = new HashMap<>();

        // 1. 싫어요 음식만 필터링하고 카테고리별 분류
        for (Food food : allFoods) {
            if (food.getPreference() != null && !food.getPreference().isLiked()) {
                Food.Category category = food.getCategory();

                if (!categoryMap.containsKey(category)) {
                    categoryMap.put(category, new ArrayList<>());
                }
                categoryMap.get(category).add(food);
            }
        }

        if (categoryMap.isEmpty()) return null;

        // 2. 가장 많은 음식 수를 가진 카테고리 찾기
        Food.Category maxCategory = null;
        int maxSize = 0;

        for (Map.Entry<Food.Category, List<Food>> entry : categoryMap.entrySet()) {
            int size = entry.getValue().size();
            if (size > maxSize) {
                maxSize = size;
                maxCategory = entry.getKey();
            }
        }

        if (maxCategory == null) return null;

        // 3. 해당 카테고리에서 랜덤 음식 추천
        List<Food> candidates = categoryMap.get(maxCategory);
        Random random = new Random();
        return candidates.get(random.nextInt(candidates.size()));
    }

    /** Queue 추천
     * 선입선출
     * 섭취 기록이 적은 음식일수록 우선순위 높게 설정
     * PriorityQueue로 빈도 기반 정렬
     * 선입선출 로직 적용
     */
    public Food recommendByQueue(List<Food> allFoods, List<MealLog> allLogs){
        Map<Integer, Integer> foodFrequency = new HashMap<>();

        for (MealLog log : allLogs) {
            int id = log.getFoodId();
            foodFrequency.put(id, foodFrequency.getOrDefault(id, 0) + 1);
        }

        // 우선순위 큐 (먹은 횟수가 적은 음식 우선)
        PriorityQueue<Food> queue = new PriorityQueue<>(
                Comparator.comparingInt(food -> foodFrequency.getOrDefault(food.getId(), 0))
        );

        queue.addAll(allFoods);

        return queue.isEmpty() ? null : queue.poll();
    }

    /** STACK 추천
     * 후입선출
     * 최근 먹은 음식을 Stack에 쌓고　제외
     *　다른카테고리　추천
     */
    public Food recommendByStack(List<Food> allFoods, List<MealLog> allLogs){
        Stack<Integer> recentFoodIds = new Stack<>();

        // 1. 최신순 정렬
        allLogs.sort((a, b) -> b.getEatenDate().compareTo(a.getEatenDate()));

        // 2. 최근 먹은 음식 ID를 중복 없이 Stack에 저장
        for (MealLog log : allLogs) {
            if (!recentFoodIds.contains(log.getFoodId())) {
                recentFoodIds.push(log.getFoodId());
            }
        }

        // 3. 가장 최근에 먹은 음식부터 pop하여 그 카테고리를 기준으로 제외
        while (!recentFoodIds.isEmpty()) {
            int recentId = recentFoodIds.pop();

            // 최근에 먹은 음식 찾기
            Food recentFood = allFoods.stream()
                    .filter(f -> f.getId() == recentId)
                    .findFirst()
                    .orElse(null);

            if (recentFood != null) {
                Food.Category recentCategory = recentFood.getCategory();

                // 다른 카테고리 음식 중 아무거나 추천
                for (Food food : allFoods) {
                    if (food.getCategory() != recentCategory) {
                        return food;
                    }
                }
            }
        }

        // 전부 같은 카테고리거나 추천할 게 없을 경우
        return null;
    }

}
