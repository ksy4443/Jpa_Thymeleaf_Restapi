package jpastudy.jpashop;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class StreamTest {
    @Test
    public void stream() {
        List<User> users = List.of(new User("길동", 10), new User("몽타", 20), new User("부트", 25));
        //User의 Name 추출해서 List<String>으로 변환해서 출력하세요
        List<String> nameList =
                users.stream() //Stream<User>
                     //.map(user -> user.getName()) //Stream<String>
                     .map(User::getName)
                     .collect(Collectors.toList()); //List<String>

        nameList.forEach(name -> System.out.println(name));
        nameList.forEach(System.out::println);

        //20살 이상인 User의 Name 추출해서 List<String>으로 변환해서 출력하세요
        users.stream()
                .filter(user -> user.getAge() >= 20)
                .forEach(user -> System.out.println(user.getName()));

        System.out.println("---------------20살 이상");
        List<String> names = users.stream() //Stream<User>
                                  .filter(user -> user.getAge() >= 20) //Stream<User>
                                  .map(user -> user.getName()) //Stream<String>
                                  .collect(Collectors.toList()); //List<String>
        names.forEach(System.out::println);

        //User들의 나이 합계
        int sum = users.stream() //Stream<User>
                .mapToInt(user -> user.getAge()) //IntStream
                .sum();
        System.out.println("나이합계 " + sum);
    }

    @Data
    @AllArgsConstructor
    static class User {
        private String name;
        private int age;
    }
}
