package service;

import model.Food;
import model.MealLog;

import java.time.LocalDate;
import java.util.*;

public class Recommender {

    private static final Random RANDOM = new Random();

    /** LIST 추천
     * 싫어요 표시한 음식 제외 후 랜덤 추천
     */
    public Food recommendByList(List<Food> allFoods) {
        List<Food> candidates = new ArrayList<>();

        for (Food food : allFoods) {
            if (food.getPreference() == null || food.getPreference().isLiked()) {
                candidates.add(food);
            }
        }

        if (!candidates.isEmpty()) {
            return candidates.get(new Random().nextInt(candidates.size()));
        }
        return null;
    }

    /** SET 추천
     * 최근 3일간 먹은 음식 ID를 Set으로 저장하고,
     * 해당 ID를 제외한 음식 중에서 무작위 추천
     */
    public Food recommendBySet(List<Food> allFoods, List<MealLog> allLogs ){
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);

        // 최근 2일간 먹은 음식 ID를 Set에 저장 (중복 제거)
        Set<Integer> recentIds = new HashSet<>();
        for (MealLog log : allLogs) {
            if (!log.getEatenDate().isBefore(threeDaysAgo)) {
                recentIds.add(log.getFoodId());
            }
        }

        // 최근에 먹지 않은 음식만 후보로
        List<Food> candidates = new ArrayList<>();
        for (Food food : allFoods) {
            if (!recentIds.contains(food.getId())) {
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
     * 싫어요가 많은 카테고리로를 묶은다음 해당 카테고리 무작위 추천
     * 편식방지
     */
    public Food recommendByMap(List<Food> allFoods) {
        Map<Food.Category, List<Food>> categoryMap = new HashMap<>();

        // 1. 싫어요 음식만 필터링하고 카테고리별 분류
        for (Food food : allFoods) {
            if (!food.getPreference().isLiked()) {
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

        return candidates.get(RANDOM.nextInt(candidates.size()));
    }

    /** STACK 추천
     * 후입선출 최근 먹은 5개 음식 제외
     * MealLog를 최신순 정렬해서 Stack에 모두 쌓기
     * Stack에서 최근 5개 음식 ID를 꺼냄 (pop)
     * 후보군 중에서 랜덤 추천
     */
    public Food recommendByStack(List<Food> allFoods, List<MealLog> allLogs){
        if (allLogs == null || allLogs.isEmpty()) return null;

        // 1. 최신순 정렬
        allLogs.sort((a, b) -> b.getEatenDate().compareTo(a.getEatenDate()));

        // 2. Stack에 모든 foodId 쌓기 (중복 제거 X)
        Stack<Integer> foodStack = new Stack<>();
        for (MealLog log : allLogs) {
            foodStack.push(log.getFoodId());
        }

        // 3. 최근에 먹은 음식 5개 pop
        Set<Integer> excludeIds = new HashSet<>();
        int count = 0;
        while (!foodStack.isEmpty() && count < 5) {
            excludeIds.add(foodStack.pop());
            count++;
        }

        // 4. 제외되지 않은 음식 추천
        List<Food> candidates = new ArrayList<>();
        for (Food food : allFoods) {
            if (!excludeIds.contains(food.getId())) {
                candidates.add(food);
            }
        }

        // 무작위 추출
        if (!candidates.isEmpty()) {
            return candidates.get(RANDOM.nextInt(candidates.size()));
        }

        return null; // 추천할 음식이 없을 경우
    }

    /** Queue 추천
     * 선입선출 가장 오래전에 먹었던 음식 추천
     * 순서가 보장된 List를 Queue로 감싸서
     * 추천이력 저장소에 넣은뒤 추천할떄마다 변경
     */
    // 프로그램 실행 중 유지될 추천 이력 저장소
    private final Set<Integer> recommendedIds = new HashSet<>();
    public Food recommendByQueue(List<Food> allFoods, List<MealLog> allLogs){
        if (allLogs == null || allLogs.isEmpty()) {
            // 섭취 기록이 없다면 아무 음식이나 추천
            return allFoods.isEmpty() ? null : allFoods.get(0);
        }

        // 1. 순서 보장된 List를 Queue로 감싸기 (오래된 기록부터)
        Queue<MealLog> eatenQueue = new LinkedList<>(allLogs);

        // 2. FIFO 순서로 순회하면서 아직 추천 안된 음식 찾기
        while (!eatenQueue.isEmpty()) {
            MealLog oldestLog = eatenQueue.poll(); // 가장 먼저 먹은 기록
            int foodId = oldestLog.getFoodId();

            // 3. 이미 추천된 적 있는 음식은 스킵
            if (recommendedIds.contains(foodId)) continue;

            // 4. 해당 음식 ID를 찾아서 리턴
            for (Food food : allFoods) {
                if (food.getId() == foodId) {
                    recommendedIds.add(foodId);  // 추천 이력 기록
                    return food;
                }
            }
        }
        // 5. 모든 음식이 이미 추천된 경우
        return null;
    }




}
