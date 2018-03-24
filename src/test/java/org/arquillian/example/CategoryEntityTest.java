package org.arquillian.example;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;

public class CategoryEntityTest {

	EntityManagerFactory factory;
	EntityManager em;
	static String testString = "This is a test";

	@Before
	public void init() {

		factory = Persistence.createEntityManagerFactory("unitTest");
		em = factory.createEntityManager();
	}

	@Test
	public void createEntityAndCheckQuery() {
		em.getTransaction().begin();
		CategoryEntity catEnt = new CategoryEntity();
		catEnt.setaValue(testString);

		em.persist(catEnt);
		em.getTransaction().commit();

		Query q = em.createQuery("select t from CategoryEntity t");
		List<CategoryEntity> catEntList = q.getResultList();
		for (CategoryEntity catEntity : catEntList) {
			assertEquals(catEntity.getaValue(), testString);
		}
		assertEquals(1, catEntList.size());
		em.close();

	}

}
