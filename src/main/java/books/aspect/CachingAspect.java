package books.aspect;

import books.model.dto.BookDto;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class CachingAspect {
    private final Map<Integer, Object> cache = new HashMap<>();

    @Pointcut("execution(* books.service.BookService.get(..))")
    public void getBookMethod() {
    }

    @Pointcut("execution(* books.service.BookService.delete(..))")
    public void deleteBookMethod() {
    }

    @Pointcut("execution(* books.service.BookService.update(..))")
    public void updateBookMethod() {
    }

    @Around("getBookMethod()")
    public Object cacheBook(ProceedingJoinPoint joinPoint) throws Throwable {
        Integer bookId = (int) joinPoint.getArgs()[0];
        String methodName = joinPoint.getSignature().getName();

        // Если результат уже есть в кеше, возвращаем его
        if (cache.containsKey(bookId)) {
            System.out.println("Returning cached result for method: " + methodName);
            return cache.get(bookId);
        }

        // Выполняем метод и сохраняем результат в кеш
        Object result = joinPoint.proceed();
        cache.put(bookId, result);
        System.out.println("Caching result for method: " + methodName);

        return result;
    }

    @After("deleteBookMethod()")
    public void invalidateCacheAfterDeleteBook(JoinPoint joinPoint) {
        Integer bookId = (int) joinPoint.getArgs()[0];
        System.out.println("Removing cached result for book with id: " + bookId);
        cache.remove(bookId);
    }

    @After("updateBookMethod()")
    public void invalidateCacheAfterUpdateBook(JoinPoint joinPoint) {
        Integer bookId = ((BookDto) joinPoint.getArgs()[0]).getId();
        System.out.println("Removing cached result for book with id: " + bookId);
        cache.remove(bookId);
    }
}
