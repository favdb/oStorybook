/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package storybook.toolkit.swing.verifier;

import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.hibernate.Session;
import storybook.SbConstants.ClientPropertyName;
import storybook.model.BookModel;
import storybook.model.hbn.dao.SbGenericDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.toolkit.I18N;

public class MultipleNumberVerifier extends AbstractInputVerifier {

	public MultipleNumberVerifier() {
		super(false);
		setCheckOnlyOnNewEntities(true);
	}

	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public boolean verify(JComponent comp) {
		if (comp instanceof JTextComponent) {
			try {
				JTextComponent tc = (JTextComponent) comp;

				BookModel documentModel = (BookModel) tc
						.getClientProperty(ClientPropertyName.DOCUMENT_MODEL
								.toString());
				if (documentModel == null) {
					throw new NullPointerException("documentModel is null");
				}

				AbstractEntity entity = (AbstractEntity) comp
						.getClientProperty(ClientPropertyName.ENTITY.toString());
				Session session = documentModel.beginTransaction();
				SbGenericDAOImpl<?, ?> dao = (SbGenericDAOImpl<?, ?>) comp
						.getClientProperty(ClientPropertyName.DAO.toString());
				dao.setSession(session);
				boolean ret = false;
				try {
					Method m = dao.getClass().getMethod("checkIfNumberExists",
							AbstractEntity.class);
					ret = (Boolean) m.invoke(dao, entity);
				} catch (NoSuchMethodException e2) {
					e2.printStackTrace();
					return false;
				}
				documentModel.commit();
				if (!ret) {
					setErrorState(ErrorState.WARNING);
					setErrorText(I18N.getMsg("msg.verifier.number.exists"));
				}
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
