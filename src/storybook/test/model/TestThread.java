package storybook.test.model;

import org.hibernate.Session;
import storybook.model.hbn.SbSessionFactory;
import storybook.model.hbn.dao.ChapterDAOImpl;

public class TestThread extends Thread {

	private SbSessionFactory sessionFactory;

	public TestThread(SbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void run() {
		System.out.println("Test03.TestThread.run(): start");
		Session session2 = sessionFactory.getSession();
		session2.beginTransaction();
		sessionFactory.query(new ChapterDAOImpl(sessionFactory.getSession()));
		session2.getTransaction().commit();
		System.out.println("Test03.TestThread.run(): finished");
	}
}
