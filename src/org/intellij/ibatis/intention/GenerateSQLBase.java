package org.intellij.ibatis.intention;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: lmeadors
 * Date: Oct 4, 2007
 * Time: 9:47:51 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GenerateSQLBase extends PsiIntentionBase {
	static Map<Integer, String> jdbcTypeNameMap = new HashMap<Integer, String>();

	static {
		populateTypeMap();
	}

	protected static void populateTypeMap() {
		Field[] fields = Types.class.getFields();
		for(Field f : fields){
			try {
				jdbcTypeNameMap.put(f.getInt(null), f.getName());
			} catch (IllegalAccessException e) {
				// let's just ignore this, ok?
			}
		}
	}
}
