package com.bbb.commerce.exim.bean;


/**
 * @author sanam jain
 * The Class ErrorVO.
 */
public class ErrorVO {
            
            /** The code. */
            private String code;
            
            /** The message. */
            private String message;
            
            /* (non-Javadoc)
             * @see java.lang.Object#toString()
             */
            public String toString() {
                final StringBuilder builder = new StringBuilder();
                builder.append("ErrorVO [code=").append(this.code)
                                .append(", message=").append(this.message)
                                .append("]");
        		return builder.toString();
            }
            
			/**
			 * Gets the code.
			 *
			 * @return the code
			 */
			public String getCode() {
				return code;
			}
			
			/**
			 * Sets the code.
			 *
			 * @param code the new code
			 */
			public void setCode(String code) {
				this.code = code;
			}
			
			/**
			 * Gets the message.
			 *
			 * @return the message
			 */
			public String getMessage() {
				return message;
			}
			
			/**
			 * Sets the message.
			 *
			 * @param message the new message
			 */
			public void setMessage(String message) {
				this.message = message;
			}
}
