package mfrolov.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mfrolov.entity.ChildEntity;
import mfrolov.entity.ParentEntity;

public class JpaTest {

    private EntityManager em;

    @Before
    public void before() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        ParentEntity activeParent = createParent(1L, State.ACTIVE);
        ParentEntity archivedParent = createParent(2L, State.ARCHIVED);

        createChild(1L, State.ACTIVE, activeParent);
        createChild(2L, State.ARCHIVED, activeParent);
        createChild(3L, State.ACTIVE, null);
        createChild(4L, State.ARCHIVED, null);
        createChild(5L, State.ARCHIVED, archivedParent);

        em.flush();
        em.clear();
    }

    @After
    public void after() {
        EntityTransaction transaction = em.getTransaction();
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }

    @Test
    public void innerJoinActiveOnly() {
        searchActiveOnly();
        assertThat(innerJoin(), containsInAnyOrder("C1:P1"));
    }

    @Test
    public void innerJoinAll() {
        searchAll();
        assertThat(innerJoin(), containsInAnyOrder("C1:P1", "C2:P1", "C5:P2"));
    }

    @Test
    public void leftJoinActiveOnly() {
        searchActiveOnly();
        assertThat(leftJoin(), containsInAnyOrder("C1:P1", "C3:null"));
    }

    @Test
    public void leftJoinAll() {
        searchAll();
        assertThat(leftJoin(), containsInAnyOrder("C1:P1", "C2:P1", "C4:null", "C3:null", "C5:P2"));
    }

    @Test
    public void oneToManyActiveOnly() {
        searchActiveOnly();
        List<ParentEntity> resultList = oneToMany();
        assertThat(toString(resultList), containsInAnyOrder("P1"));

        ParentEntity parent = resultList.get(0);
        List<ChildEntity> children = parent.getChildren();
        assertThat(toString(children), containsInAnyOrder("C1"));
    }

    private List<String> toString(List<?> list) {
        // EclipseLink doesn't work with Java8
        // See http://stackoverflow.com/questions/26816650/java8-collections-sort-sometimes-does-not-sort-jpa-returned-lists
        List<String> resultList = new ArrayList<>();
        for (Object o : list) {
            resultList.add(o.toString());
        }
        return resultList;
    }

    private List<String> innerJoin() {
        return toString(em.createQuery(
                "select new mfrolov.test.ChildParentDto(c.description, p.description) from ChildEntity c join ParentEntity p on c.parent = p",
                ChildParentDto.class).getResultList());
    }

    private void searchActiveOnly() {
        Set<String> param = new HashSet<>();
        param.add("0");
        em.setProperty("archived", param);
    }

    private void searchAll() {
        Set<String> param = new HashSet<>();
        param.add("0");
        param.add("1");
        em.setProperty("archived", param);
    }

    private ParentEntity createParent(Long id, State state) {
        ParentEntity parent = new ParentEntity();
        parent.setId(id);
        parent.setArchived(state == State.ARCHIVED ? "1" : "0");
        parent.setDescription(String.format("P%d", id));
        em.persist(parent);
        return parent;
    }

    private void createChild(Long id, State state, ParentEntity parentEntity) {
        ChildEntity child = new ChildEntity();
        child.setId(id);
        child.setDescription(String.format("C%d", id));
        child.setArchived(state == State.ARCHIVED ? "1" : "0");
        if (parentEntity != null) {
            child.setParent(parentEntity);
            parentEntity.getChildren().add(child);
        } else {
            em.persist(child);
        }
    }

    private enum State {
        ACTIVE,
        ARCHIVED
    }

    private List<ParentEntity> oneToMany() {
        TypedQuery<ParentEntity> query = em.createQuery("select p from ParentEntity p", ParentEntity.class);
        return query.getResultList();
    }

    private List<String> leftJoin() {
        return toString(em.createQuery(
                "select new mfrolov.test.ChildParentDto(c.description, p.description) from ChildEntity c left join ParentEntity p on c.parent = p",
                ChildParentDto.class).getResultList());
    }

}
