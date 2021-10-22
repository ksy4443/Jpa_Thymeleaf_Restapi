import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GroupByTest {
    @Test
    public void groupby() {
        List<Dish> dishList = Arrays.asList(
                new Dish("pork", 700, Type.MEAT),
                new Dish("spagetti", 550, Type.NOODLE),
                new Dish("tomato", 200, Type.VEGE),
                new Dish("onion", 150, Type.VEGE)
        );
        //Dish의 이름만 출력하기
        List<String> nameList = dishList.stream() //Stream<Dish>
                .map(Dish::getName) //Stream<String>
                .collect(Collectors.toList());
        nameList.forEach(dishName -> System.out.println(dishName));
        //Dish의 이름을 구분자를 포함한 문자열로 출력하기
        String nameStrs = dishList.stream() //Stream<Dish>
                .map(dish -> dish.getName()) //Stream<String>
                .collect(Collectors.joining(","));
        System.out.println(nameStrs);
    }

    static class Dish {
        String name;
        int calory;
        Type type;

        public Dish(String name, int calory, Type type) {
            this.name = name;
            this.calory = calory;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public int getCalory() {
            return calory;
        }

        public Type getType() {
            return type;
        }

    }

    enum Type {
        MEAT, NOODLE, VEGE
    }
}
