import java.time.LocalTime;

import junit.framework.Assert;

import org.bravo.activitywatch.NameParser;
import org.junit.Test;


public class TimeParseTest {

	@Test
	public void test() {
		LocalTime time = NameParser.parseName("Test Dummy 12h");
		Assert.assertEquals(12, time.getHour());
	}
	
	@Test
	public void test2() {
		LocalTime time = NameParser.parseName("Test Dummy 48s");
		Assert.assertEquals(48, time.getSecond());
	}
	
	@Test
	public void test3() {
		LocalTime time = NameParser.parseName("Test Dummy 34m");
		Assert.assertEquals(34, time.getMinute());
	}
	
	@Test
	public void test4() {
		LocalTime time = NameParser.parseName("Test Dummy 7h34m");
		Assert.assertEquals(7, time.getHour());
		Assert.assertEquals(34, time.getMinute());
	}
	
	@Test
	public void test5() {
		LocalTime time = NameParser.parseName("Test Dummy 12h34m48s");
		Assert.assertEquals(12, time.getHour());
		Assert.assertEquals(34, time.getMinute());
		Assert.assertEquals(48, time.getSecond());
	}

}
