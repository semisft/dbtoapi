package com.semiz.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.semiz.db.entity.ParameterException;

public class ServiceItemParameter {
	ParameterType type;
	String name;
	DataType dataType;
	String description;

	public ServiceItemParameter() {

	}

	public ParameterType getType() {
		return type;
	}

	public void setType(ParameterType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public Object convertToType(Object valueObjectOrList) {
		Object result = null;
		try {
			if (valueObjectOrList == null) {
				result = valueObjectOrList;
			}
			else {
				if ((valueObjectOrList instanceof List)) {
					List valueList = (List) valueObjectOrList;
					result = new ArrayList();
					for (ListIterator iterator = valueList.listIterator(); iterator.hasNext();) {
						Object val = (Object) iterator.next();
						val = convertStringToDataType(val);
						((List)result).add(val);
					}
					if (valueList.size() == 1) {
						result = ((List)result).get(0);
					}
				}
				else {
					result = convertStringToDataType(valueObjectOrList);
				}
			}
		} catch (Exception e) {
			throw new ParameterException(this.getName(), valueObjectOrList, "trying to convert to " + this.getDataType());
		}
		return result;
	}

	private Object convertStringToDataType(Object value) {
		Object result = null;
		if (DataType.STRING.equals(this.getDataType())) {
			result = value;
		} else if (DataType.INTEGER.equals(this.getDataType())) {
			// TODO: long or short
			result = new Integer(value.toString());
		} else if (DataType.DECIMAL.equals(this.getDataType())) {
			result = new BigDecimal(value.toString());
		}
		return result;
	}

}
