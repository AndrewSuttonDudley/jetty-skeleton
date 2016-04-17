package skeleton.util;

import java.util.Date;

public class DateUtil {
	
	private DateUtil() {
		
	}
	
	public static long timeDiff(Date date, Date subtrahand) {
		return date.getTime() - subtrahand.getTime();
	}
}
