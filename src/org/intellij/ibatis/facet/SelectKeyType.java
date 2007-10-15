package org.intellij.ibatis.facet;

/**
 * Created by IntelliJ IDEA.
 * User: lmeadors
 * Date: Oct 13, 2007
 * Time: 5:32:18 AM
 * To change this template use File | Settings | File Templates.
 */
public enum SelectKeyType {
	none("None"), preInsert("Pre-insert"), postInsert("Post-insert");
	private String description;

	SelectKeyType(String description) {
		this.description = description;
	}

	public String toString() {
		return description;
	}
}
