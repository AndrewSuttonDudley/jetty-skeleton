package skeleton.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettySkeletonServletResponseWrapper extends HttpServletResponseWrapper {

	private static final Logger logger = LoggerFactory.getLogger(JettySkeletonServletResponseWrapper.class);
	
	TeeServletOutputStream tsos;
	
	PrintWriter teeWriter;
	
	ByteArrayOutputStream baos;
	
	public JettySkeletonServletResponseWrapper(HttpServletResponse response) {
		super(response);
	}
	
	public String getContent() {
		if (baos == null) {
			return "";
		}
		return baos.toString();
	}
	
	@Override
	public PrintWriter getWriter() throws IOException {
		
		if (teeWriter == null) {
			teeWriter = new PrintWriter(new OutputStreamWriter(getOutputStream()));
		}
		return teeWriter;
	}
	
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		
		if (tsos == null) {
			baos = new ByteArrayOutputStream();
			tsos = new TeeServletOutputStream(getResponse().getOutputStream(), baos);
		}
		return tsos;
	}
	
	@Override
	public void flushBuffer() throws IOException {
		if (tsos != null) {
			tsos.flush();
			logger.debug("Flushing tsos");
		}
		if (teeWriter != null) {
			teeWriter.flush();
			logger.debug("Flushing teeWriter");
		}
	}
	
	public class TeeServletOutputStream extends ServletOutputStream {
		
		private final TeeOutputStream targetStream;
		
		public TeeServletOutputStream(OutputStream osOne, OutputStream osTwo) {
			targetStream = new TeeOutputStream(osOne, osTwo);
		}
		
		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {
		}

		@Override
		public void write(int b) throws IOException {
			targetStream.write(b);
		}
	}
}
