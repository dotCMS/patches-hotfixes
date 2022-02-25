package com.dotcms.security.multipartrequest;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MultiPartSecurityRequestWrapper extends HttpServletRequestWrapper {

    public byte[] getBody() {
        return body;
    }

    public void setBody(final byte[] body) {
        this.body = body;
    }

    private byte[] body;

    public MultiPartSecurityRequestWrapper(final HttpServletRequest request) throws IOException {
        super(request);
        try {
            body = IOUtils.toByteArray(super.getInputStream());
        } catch (NullPointerException e){
            // Quiet
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStreamImpl(new ByteArrayInputStream(body));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        String enc = getCharacterEncoding();
        if (enc == null) {
            enc = "UTF-8";
        }
        return new BufferedReader(new InputStreamReader(getInputStream(), enc));
    }

    private class ServletInputStreamImpl extends ServletInputStream {

        private final InputStream is;
        private boolean closed = false;

        public ServletInputStreamImpl(InputStream is) {
            this.is = is;
        }

        public void close() throws IOException {
            this.is.close();
            this.closed = true;
        }

        public int read() throws IOException {
            return is.read();
        }

        public boolean markSupported() {
            return this.is.markSupported();
        }

        public synchronized void mark(int i) {
            this.is.mark(i);
        }

        public synchronized void reset() throws IOException {
            this.is.reset();
        }

        @Override
        public boolean isFinished() {
            return this.closed;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}
