package org.arquillian.example;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;

public class CategoryEntityTest {

	EntityManagerFactory factory;
	EntityManager em;
	

	@Before
	public void init() {
		factory = Persistence.createEntityManagerFactory("unitTest");
		em = factory.createEntityManager();
	}

	@Test
	public void createEntityAndCheckQuery() throws InterruptedException {
		String initialValue = "initial String Value";
		createAndPersisteEntity(initialValue);
		List<CategoryEntity> catEntList = getListFromDB();
		assertEquals(initialValue,catEntList.get(0).getaValue() );
		assertEquals(1, catEntList.size());
		final String value1 ="new string1";
		Thread fstThread = new Thread() {
		    public void run() {
		    		System.out.println("------before wait fstThread");
		    	   changeFirstEntryWithDelay(catEntList, 925, value1);
		    		System.out.println("-------after wait fstThread");
		    }
		};
		fstThread.start();		
		Thread.sleep(200);
		String returnedEntValue1 = getFirstEntityValueAgain();
		assertEquals(initialValue, returnedEntValue1);
		
		final String value2 ="new string2";
		changeFirstEntryWithDelay(catEntList, 1, value2);
		fstThread.join();
		String returnedEntValue2 = getFirstEntityValueAgain();
		
		
		assertEquals(value2, returnedEntValue2);
		
	}

	private void changeFirstEntryWithDelay(List<CategoryEntity> catEntList, Integer delay, String value) {
		changeFirstEntity(catEntList, (i) -> {
			try {
				Thread.sleep(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, delay, value);
	}

	private String getFirstEntityValueAgain() {		
	    em.clear();
		Query q2 = em.createQuery("select t from CategoryEntity t");
		List<CategoryEntity> catEntList2 = q2.getResultList();
		String firstEntityValue = catEntList2.get(0).getaValue();
		System.out.println(Thread.currentThread().getName()+"-------catEntList2(0).id:"+ catEntList2.get(0).getId());
		System.out.println(Thread.currentThread().getName()+"-------after getFirstEntityValueAgain():"+ firstEntityValue);
		return firstEntityValue;
	}

	private void changeFirstEntity(List<CategoryEntity> catEntList, IntConsumer consumer, Integer parameter,String newValue) {
		System.out.println(Thread.currentThread().getName()+"-------bbbbbbbefore transaction AAANNNND Read  start  value:"+newValue);
		EntityManager myEntManager = factory.createEntityManager();
		myEntManager.getTransaction().begin();
		
		CategoryEntity myEntity = myEntManager.find(CategoryEntity.class, catEntList.get(0).getId(),LockModeType.PESSIMISTIC_WRITE);
		consumer.accept(parameter);
		
		System.out.println(Thread.currentThread().getName()+"-------aaaaaaaafter transaction start  value:"+newValue);
		myEntity.setaValue(newValue);
		myEntManager.merge(myEntity);
		CategoryEntity myEntity2 = myEntManager.find(CategoryEntity.class, catEntList.get(0).getId());
		System.out.println(Thread.currentThread().getName()+"------acacacacacacacac find after merge/commit  value:"+myEntity2.getaValue());
		
		myEntManager.getTransaction().commit();
		myEntManager.close();
		System.out.println(Thread.currentThread().getName()+"------acacacacacacacac after merge/commit  value:"+myEntity.getaValue());
	}

	private List<CategoryEntity> getListFromDB() {
		em.clear();
		Query q = em.createQuery("select t from CategoryEntity t",CategoryEntity.class);		
		List<CategoryEntity> catEntList = q.getResultList();		
		return catEntList;
	}

	private void createAndPersisteEntity(String value) {
		EntityTransaction transact = em.getTransaction();
		transact.begin();
	
		CategoryEntity catEnt = new CategoryEntity();
		
		catEnt.setaValue(value);

		em.persist(catEnt);
		LockModeType lm = em.getLockMode(catEnt);
		System.out.println("lllllllllllllockmode:"+lm.name());
		transact.commit();
	}

}
