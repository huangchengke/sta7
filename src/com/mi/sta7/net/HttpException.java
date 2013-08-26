
package com.mi.sta7.net;


/**
 * 请求服务器时抛出的异常
 * @author frand
 */
public class HttpException extends Exception {

	private static final long serialVersionUID = 475022994858770424L;
	private int statusCode = -1;
	
    public HttpException(String msg) {
        super(msg);
    }

    public HttpException(Exception cause) {
        super(cause);
    }

    public HttpException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public HttpException(String msg, Exception cause) {
        super(msg, cause);
    }

    public HttpException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
    
	public HttpException() {
		super(); 
	}

	public HttpException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public HttpException(Throwable throwable) {
		super(throwable);
	}

	public HttpException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
}
