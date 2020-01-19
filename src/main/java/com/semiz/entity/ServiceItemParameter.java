package com.semiz.entity;

import java.math.BigDecimal;
import java.util.List;

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

	public Object convertToType(Object valueList) {
		Object result = null;
		try {
			if (valueList == null) {
				result = valueList;
			}
			// TODO: add list type
			else {
				Object value = valueList;
				if ((valueList instanceof List) && ((List) valueList).size() == 1) {
					value = ((List) valueList).get(0);
				}
				if (DataType.STRING.equals(this.getDataType())) {
					result = value;
				} else if (DataType.INTEGER.equals(this.getDataType())) {
					// TODO: long or short
					result = new Integer(value.toString());
				} else if (DataType.DECIMAL.equals(this.getDataType())) {
					result = new BigDecimal(value.toString());
				}
			}
		} catch (Exception e) {
			throw new ParameterException(this.getName(), valueList, "trying to convert to " + this.getDataType());
		}
		return result;
	}

}
