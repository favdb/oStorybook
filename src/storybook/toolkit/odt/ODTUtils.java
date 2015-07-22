package storybook.toolkit.odt;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.jdom.Document;
import org.jdom.Element;
import org.jopendocument.dom.ODPackage;

import storybook.SbApp;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;

public final class ODTUtils {

	private ODTUtils() {
        // never used.
	}
	
	/**
	 * get book size in characters (including spaces).
	 * @param mainFrame to use
	 * @return the number of characters
	 */
	public static int getBookSize(MainFrame mainFrame) {
		Map<Scene, Integer> sizes = getScenesSize(mainFrame);
        int ret = 0;
		for (Integer value : sizes.values()) {
			ret += value;
		}
		return ret;
	}

	/**
	 * get book size in words.
	 * @param mainFrame to use
	 * @return the number of words
	 */
	public static int getBookWords(MainFrame mainFrame) {
		Map<Scene, Integer> sizes = getScenesWords(mainFrame);
        int ret = 0;
		for (Integer value : sizes.values()) {
			ret += value;
		}
		return ret;
	}
	
	public static Map<Object, Integer> getElementsSize(MainFrame mainFrame) {
	    Map<Object, Integer> sizes = new HashMap<Object, Integer>();
		// Get scenes
		sizes.clear();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl partdao = new PartDAOImpl(session);
		List<Part> roots = partdao.findAllRoots();
		session.close();
		
		for (Part root : roots) {
			appendElementSizes(mainFrame, root, sizes);
		}
		
		return sizes;
	}
	
	private static int appendElementSizes(MainFrame mainFrame, Part part,
			 Map<Object, Integer> sizes) {

		int ret = 0;
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl partdao = new PartDAOImpl(session);
		List<Part> subparts = partdao.getParts(part);
		List<Chapter> chapters = partdao.findChapters(part); 
		session.close();
		
		for (Part subpart : subparts) {
			ret += appendElementSizes(mainFrame, subpart, sizes);
		}
		for (Chapter chapter : chapters) {
			int chapterSize = appendElementSizes(mainFrame, chapter, sizes);
			ret += chapterSize;
		}
		sizes.put(part, ret);
		SbApp.trace(part.getName() + " " + ret);
		return ret;
	}

	private static int appendElementSizes(MainFrame mainFrame, Chapter chapter,
			Map<Object, Integer> sizes) {

		int ret = 0;
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findByChapter(chapter);
		session.close();
		
		for (Scene scene : scenes) {
			int sceneSize = 0;
			if (BookUtil.isUseLibreOffice(mainFrame)) {
				String filepath = scene.getOdf();
				if ((filepath == null) || (filepath.isEmpty())) {
					filepath = ODTUtils.getFilePath(mainFrame, scene);
				}
				sceneSize = ODTUtils.getDocumentSize(filepath);
			} else {
				sceneSize = scene.getSummary().length();
			}
			sizes.put(scene, sceneSize);
			SbApp.trace(scene.getTitle() + " " + sceneSize);
			ret += sceneSize;
		}
		sizes.put(chapter, ret);
		SbApp.trace(chapter.getTitle() + " " + ret);
		return ret;
	}
	
	public static Map<Scene, Integer> getScenesSize(MainFrame mainFrame) {
		return getScenesSizeOrWords(mainFrame, false);
	}
	
	public static Map<Scene, Integer> getScenesWords(MainFrame mainFrame) {
		return getScenesSizeOrWords(mainFrame, true);
	}
	
	private static Map<Scene, Integer> getScenesSizeOrWords(MainFrame mainFrame, boolean wordsCount) {
	    Map<Scene, Integer> sceneSizes = new HashMap<Scene, Integer>();
		// Get scenes
		sceneSizes.clear();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl partdao = new PartDAOImpl(session);
		partdao.findAllRoots();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findAll();
		session.close();

		// Get size of scenes from LibreOffice or summary
		SbApp.trace("computing sizes");
		for (Scene scene : scenes) {
			if (BookUtil.isUseLibreOffice(mainFrame)) {
				String filepath = scene.getOdf();
				if ((filepath == null) || (filepath.isEmpty())) {
					filepath = ODTUtils.getFilePath(mainFrame, scene);
				}
				sceneSizes.put(scene, ODTUtils.getDocumentSizeOrWords(filepath, wordsCount));
			} else {
				if (wordsCount)
				{
					int size = scene.getSummary().split("\\w+").length;
					sceneSizes.put(scene, size);
				}
				else
				{
				   sceneSizes.put(scene, scene.getSummary().length());
				}
			}
		}
		return sceneSizes;
	}
	
	public static int getSize(MainFrame mainFrame, Object object) {
		
		int ret = 0;
		if (object instanceof Scene)
		{
			if (BookUtil.isUseLibreOffice(mainFrame)) {
				String filepath = ((Scene)object).getOdf();
				if ((filepath == null) || (filepath.isEmpty())) {
					filepath = ODTUtils.getFilePath(mainFrame, (Scene)object);
				}
				ret = getDocumentSize(filepath);
			} else {
				ret = ((Scene)object).getSummary().length();
			}
		} else if (object instanceof Chapter) {
			Chapter chapter = (Chapter)object;
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			ChapterDAOImpl dao = new ChapterDAOImpl(session);
			List<Scene> scenes = dao.findScenes(chapter);
			session.close();
			
			for (Scene scene : scenes) {
			    ret += getSize(mainFrame, scene);
			}
		} else if (object instanceof Part) {
			Part part = (Part)object;
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			PartDAOImpl partdao = new PartDAOImpl(session);
			List<Chapter> chapters = partdao.findAllChapters(part);
			session.close();
			
			for (Chapter chapter : chapters) {
			    ret += getSize(mainFrame, chapter);
			}
		}
		
		return ret;
	}

	public static String getFilePath(MainFrame mainFrame, Scene scene) {
		String path = mainFrame.getDbFile().getPath();
		String str1 = ""+scene.getChapter().getChapterno();
		if (str1.length() < 2) {str1 = "0" + str1;}
		String str2 = ""+scene.getSceneno();
		if (str2.length() < 2) {str2 = "0" + str2;}
		String str = path + File.separator
			+ I18N.getMsg("msg.common.chapter") +str1 + "-"
			+ I18N.getMsg("msg.common.scene") + str2
			+ ".odt";
		SbApp.trace("Scene odt file=" + str);
		return(str);
	}

	/**
	 * Get character size of an ODT file.
	 * @param filePath path to the file
	 * @return the size in characters (approximated) or zero.
	 */
	public static int getDocumentSize(String filePath) {
		return getDocumentSizeOrWords(filePath,false);
	}

	/**
	 * Get words size of an ODT file.
	 * @param filePath path to the file
	 * @return the size in words (approximated) or zero.
	 */
	public static int getDocumentWords(String filePath) {
		return getDocumentSizeOrWords(filePath,true);
	}

	/**
	 * Get character size of an ODT file.
	 * @param filePath path to the file
	 * @param wordCount true to count words instead of characters
	 * @return the size in characters (approximated) or zero.
	 */
	public static int getDocumentSizeOrWords(String filePath, boolean countWords) {
		int ret = 0;
		if (filePath != null) {
		
			ODPackage pkg;
			try {
				File file = new File(filePath);
				if (file.exists()) {
					pkg = new ODPackage(file);
					Document doc = pkg.getDocument("content.xml");
					ret = getSize(doc.getRootElement(), countWords);
				}
			} catch (IOException e) {
			}
		}
		return ret;
	}

	/**
	 * Get size of an element, along with all its children size.
	 * @param elt to inspect
	 * @param wordCount true to count words instead of characters
	 * @return the size found
	 */
	private static int getSize(Element elt, boolean wordCount) {
		String txt = elt.getText();
		int ret = 0;
		if (wordCount)
		{
			ret = txt.split("\\w+").length ;
		}
		else
		{
			ret = txt.length();
		}
		@SuppressWarnings("unchecked")
		List<Object> objs1 = elt.getContent();
		for (Object o : objs1) {
			if (o instanceof Element) {
				ret += getSize((Element) o, wordCount);
			}
		}
		return ret;
	}
}
