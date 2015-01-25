package com.almondarts.collections;


public interface MapMapping<FK,FV,TK,TV> {

	TK key(FK key,FV value);
	TV value(FK key,FV value);

}
