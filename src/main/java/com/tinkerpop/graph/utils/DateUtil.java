package com.tinkerpop.graph.utils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.math.RandomUtils;

public class DateUtil {
	
	public static long getRandomDate() {
		
		Random random = new Random();
		int minDay = (int) LocalDate.of(2016, 1, 1).toEpochDay();
		int maxDay = (int) LocalDate.of(2016, 4, 6).toEpochDay();
		long randomDay = minDay + random.nextInt(maxDay - minDay);

		//LocalDate randomDate = LocalDate.ofEpochDay(randomDay).;
		
		return randomDay;
		
		//return new Date(Math.abs(System.currentTimeMillis() - RandomUtils.nextLong())).getTime();
	}

}
