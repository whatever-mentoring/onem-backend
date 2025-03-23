package community.whatever.onembackendjava.runner;

import community.whatever.onembackendjava.util.DummyDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 더미 데이터 생성을 위한 CommandLineRunner
 * 
 * 사용 방법:
 * 애플리케이션 실행 시 아래와 같이 프로필과 파라미터를 지정하여 실행합니다.
 * --spring.profiles.active=dummy-data --data.count=100000
 */
@Component
@Profile("dummy-data")
@RequiredArgsConstructor
public class DummyDataRunner implements CommandLineRunner {

    private final DummyDataGenerator dummyDataGenerator;
    private final ConfigurableApplicationContext context;

    @Override
    public void run(String... args) {
        System.out.println("더미 데이터 생성 프로세스 시작...");
        
        // 기본값은 10만개
        int count = 100000;
        
        // 커맨드 라인 인자에서 데이터 개수 추출
        for (String arg : args) {
            if (arg.startsWith("--data.count=")) {
                try {
                    count = Integer.parseInt(arg.substring("--data.count=".length()));
                } catch (NumberFormatException e) {
                    System.err.println("유효한 데이터 개수를 지정해주세요. 기본값 100000개가 사용됩니다.");
                }
                break;
            }
        }
        
        try {
            // 더미 데이터 생성 실행
            dummyDataGenerator.generateDummyUrls(count);
            System.out.println("더미 데이터 생성 완료!");
        } catch (Exception e) {
            System.err.println("더미 데이터 생성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 작업 완료 후 애플리케이션 종료
            System.out.println("애플리케이션을 종료합니다...");
            SpringApplication.exit(context, () -> 0);
        }
    }
}
