package books.storage;

import books.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@Transactional
public class BookStorageHibernateImpl implements BookStorage {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Book book) {
        entityManager.persist(book);
    }

    @Override
    public Optional<Book> get(Integer id) {
        // Получаем CriteriaBuilder из EntityManager
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Создаем CriteriaQuery для сущности Book
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);

        // Указываем корень (основную сущность) запроса
        Root<Book> bookRoot = criteriaQuery.from(Book.class);

        // Определяем условие выборки по id
        criteriaQuery.select(bookRoot)
                .where(criteriaBuilder.equal(bookRoot.get("id"), id));

        Query query = entityManager.createQuery(criteriaQuery);
        query.setHint("org.hibernate.cacheable", "true");
        // Выполняем запрос
        List<Book> result = entityManager.createQuery(criteriaQuery).getResultList();

        if (!result.isEmpty()) {
            Book book = result.get(0);
            Hibernate.initialize(book.getGenres());
            Hibernate.initialize(book.getAuthors());
            return Optional.of(book);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void update(Book updatedBook) {
        entityManager.merge(updatedBook);
    }

    @Override
    public void delete(Integer id) {
        entityManager.createQuery("DELETE FROM Book b WHERE b.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public List<Book> findAll() {
        List<Book> books =  entityManager.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        for (Book book : books) {
            Hibernate.initialize(book.getAuthors());
            Hibernate.initialize(book.getGenres());
        }
        return books;
    }
}
