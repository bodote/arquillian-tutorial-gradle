package org.arquillian.example;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
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
	public void jsonBMapEntity() throws FileNotFoundException {
	
		//Annotation @JsonbTypeAdapter(CategoryEntityJsonObjectAdapter.class) does not work due to a  bug in yasson
		//JsonbConfig config = new JsonbConfig().withAdapters(new CategoryEntityJsonObjectAdapter());
		
		String responseJsonResourceName = "/post/client/request/json_catEnt.json";
		InputStream inputStream = getClass().getResourceAsStream(responseJsonResourceName);
		//String jsonString = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
		CategoryEntity categoryEntity = JsonbBuilder.create().fromJson(inputStream, CategoryEntity.class);
		
		assertEquals("string1", categoryEntity.getaValue());
		
	}
	@Test
	public void createEntityAndCheckQuery() throws InterruptedException {
		String initialValue = "initial String Value";
		createAndPersisteEntity(initialValue);
		List<CategoryEntity> catEntList = getListFromDB();
		assertEquals(initialValue,catEntList.get(0).getaValue() );
		assertEquals(1, catEntList.size());
		final String value1 ="new string1";
		// one of the both concurrent attempts to change need to run in a separate thread:
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

	private void changeFirstEntryWithDelay(List<CategoryEntity> catEntList, Integer delayTime, String value) {
		changeFirstEntityafterBackEndCall(catEntList, (time) -> {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, delayTime, value);
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

	private void changeFirstEntityafterBackEndCall(List<CategoryEntity> catEntList, IntConsumer backEndCall, Integer backEndCallParameter,String newValue) {
		System.out.println(Thread.currentThread().getName()+"-------bbbbbbbefore transaction AAANNNND Read  start  value:"+newValue);
		//create and use a NEW Entity Manager to test the cache behavior for distinct EntityManager instances
		EntityManager myEntManager = factory.createEntityManager();
		myEntManager.getTransaction().begin();		
		//get the first Object from DB
		CategoryEntity myEntity = myEntManager.find(CategoryEntity.class, catEntList.get(0).getId(),LockModeType.PESSIMISTIC_WRITE);
		//call the back end , which will take a while
		backEndCall.accept(backEndCallParameter);
		
		System.out.println(Thread.currentThread().getName()+"-------aaaaaaaafter transaction start  value:"+newValue);
		//change the 1st objects value
		myEntity.setaValue(newValue);
		myEntManager.merge(myEntity);
		CategoryEntity myEntity2 = myEntManager.find(CategoryEntity.class, catEntList.get(0).getId());
		System.out.println(Thread.currentThread().getName()+"------acacacacacacacac find after merge/commit  value:"+myEntity2.getaValue());
		//write back to DB
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
