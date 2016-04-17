package skeleton.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettySkeletonServletRequestWrapper extends HttpServletRequestWrapper {
	
	private static final Logger logger = LoggerFactory.getLogger(JettySkeletonServletRequestWrapper.class);
	
	private final String payload;
	
	public JettySkeletonServletRequestWrapper(HttpServletRequest request) {
		super(request);
		
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader reader = null;
		try {
			InputStream is = request.getInputStream();
			if (is != null) {
				reader = new BufferedReader(new InputStreamReader(is));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = reader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException e) {
			logger.error("Error reading the request payload", e);
		}
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error("This shouldn't happen", e);
			}
		}
		payload = stringBuilder.toString();
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		
		final ByteArrayInputStream bais = new ByteArrayInputStream(payload.getBytes());
		ServletInputStream sis = new ServletInputStream() {
			
			private boolean finished = false;
			
			@Override
			public int read() throws IOException {
				int byteRead = bais.read();
				if (byteRead == -1) {
					finished = true;
				}
				return byteRead;
			}

			@Override
			public boolean isFinished() {
				return finished;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
			}
		};
		return sis;
	}
	
	public String getPayload() {
		return payload;
	}
}
