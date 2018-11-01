/**
 * rohan magare, 001231457, magare.r@husky.neu.edu
 * ritesh gupta, 001280361, gupta.rite@husky.neu.edu
 * pratiksha shetty, 00121643697, shetty.pr@husky.neu.edu
 * yogita jain, 001643815, jain.yo@husky.neu.edu
 **/
package com.csye6225.demo.Service;

public class FileArchiveServiceException extends RuntimeException {

    private static final long serialVersionUID = 2468434988680850339L;


    /**
     *
     * @param s
     * @param ex
     */
    public FileArchiveServiceException(String s, Throwable ex) {
        super(s, ex);
    }
}
