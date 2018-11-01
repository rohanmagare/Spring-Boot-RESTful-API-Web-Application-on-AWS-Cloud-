/**
 * rohan magare, 001231457, magare.r@husky.neu.edu
 * ritesh gupta, 001280361, gupta.rite@husky.neu.edu
 * pratiksha shetty, 00121643697, shetty.pr@husky.neu.edu
 * yogita jain, 001643815, jain.yo@husky.neu.edu
 **/
package com.csye6225.demo.controllers;

import com.csye6225.demo.Service.FileArchiveService;
import com.csye6225.demo.dao.MediaFileUploadDao;
import com.csye6225.demo.dao.PersistTaskDao;
import com.csye6225.demo.dao.UserDao;
import com.csye6225.demo.entity.MediaFile;
import com.csye6225.demo.entity.Task;
import com.csye6225.demo.entity.User;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Controller
public class
HomeController {


    private final static Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private PersistTaskDao taskDao;
    @Autowired
    private MediaFileUploadDao fileUploadDao;
    @Autowired
    private FileArchiveService fileArchiveService;


    /**
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody()
    public String home() throws Exception {

        JsonObject jsonO = new JsonObject();
        jsonO.addProperty("message", "Home Page. Use /login.htm for login & /register.htm for register");
        return jsonO.toString();
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
    protected @ResponseBody
    String registerNewUser(HttpServletRequest request, HttpServletResponse response, @RequestBody User userReq) throws Exception {

        JsonObject j = new JsonObject();
        try {
            User user = new User();
            if ((!StringUtils.isBlank(userReq.getEmailId())) && (!StringUtils.isBlank(userReq.getPassword())) && (userDao.findUserByEmailId(userReq.getEmailId()) == null)) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = passwordEncoder.encode(userReq.getPassword());
                user.setPassword(hashedPassword);
                user.setEmailId(userReq.getEmailId());
                userDao.save(user);
                j.addProperty("message", "User Registered");
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                j.addProperty("error", "User not registered. Email Id or password not entered or it already exists.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }

        } catch (IllegalStateException e) {
            j.addProperty("Exception", e.toString());

        } catch (Exception e) {
            j.addProperty("Exception", e.toString());
        }
        return j.toString();
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    protected @ResponseBody
    String login(HttpServletRequest request, HttpServletResponse response) throws Exception {

        JsonObject jsonObject = new JsonObject();
        try {
            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Basic")) {
                // Authorization: Basic base64credentials
                String base64Credentials = authorization.substring("Basic".length()).trim();
                String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                        Charset.forName("UTF-8"));
                // credentials = username:password
                final String[] values = credentials.split(":", 2);
                User user = userDao.findUserByEmailId(values[0]);
                if (user != null) {
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    boolean flag = passwordEncoder.matches(values[1], user.getPassword());
                    if (flag) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        jsonObject.addProperty("message", " welcome " + values[0].toString() + " !! you are logged in. The current time is " + new Date().toString());
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        jsonObject.addProperty("Error", "Invalid User Credentials");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    jsonObject.addProperty("Error", "Invalid User Credentials");
                }

            }
        } catch (IllegalStateException e) {
            jsonObject.addProperty("Exception", e.toString());

        } catch (Exception e) {
            jsonObject.addProperty("Exception", e.toString());
        }
        return jsonObject.toString();
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tasks", method = RequestMethod.POST, produces = "application/json")
    protected @ResponseBody
    String createUserTask(HttpServletRequest request, HttpServletResponse response, @RequestBody Task taskReq) throws Exception {

        JsonObject j = new JsonObject();
        try {
            User user = authenticateUser(request);
            if (user != null) {
                Task task = new Task();
                if ((!StringUtils.isBlank(taskReq.getDescription())) && (taskReq.getDescription().length() < 100)) {
                    task.setDescription(taskReq.getDescription());
                    task.setUser(user);
                    taskDao.save(task);
                    j.addProperty("message", "Task Created");
                    response.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    j.addProperty("Error", "Description is EMPTY or exceeds maximum allowable length");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                j.addProperty("Error", "Invalid User Credentials");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (IllegalStateException e) {
            j.addProperty("Exception", e.toString());

        } catch (Exception e) {
            j.addProperty("Exception", e.toString());
        }
        return j.toString();
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public @ResponseBody
    String deleteTask(@PathVariable("id") long id, HttpServletRequest request, HttpServletResponse response) {

        JsonObject j = new JsonObject();
        try {
            User user = authenticateUser(request);
            if (user != null) {
                Task task = taskDao.findByTaskId(id);
                if (task != null) {
                    if (user == task.getUser()) {
                        taskDao.delete(task);
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                        j.addProperty("message", "Task Deleted");
                    } else {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        j.addProperty("Error", "Access Denied. Task belongs to a different user.");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    j.addProperty("Error", "Use correct ID.");
                }
            } else {
                j.addProperty("Error", "Invalid User Credentials");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (IllegalStateException e) {
            j.addProperty("Exception", e.toString());

        } catch (Exception e) {
            j.addProperty("Exception", e.toString());
        }
        return j.toString();
    }

    /**
     * @param id
     * @param task
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.PUT, produces = "application/json")
    public @ResponseBody
    String updateTasks(@PathVariable("id") long id, @RequestBody Task task, HttpServletRequest request, HttpServletResponse response) {

        JsonObject j = new JsonObject();
        try {
            User user = authenticateUser(request);
            if (user != null) {
                Task currentTask = taskDao.findByTaskId(id);
                if (currentTask != null) {
                    if (user == currentTask.getUser()) {
                        if ((!StringUtils.isBlank(task.getDescription())) && (task.getDescription().length() < 100)) {
                            currentTask.setDescription(task.getDescription());
                            taskDao.save(currentTask);
                            j.addProperty("message", "Task Updated");
                            response.setStatus(HttpServletResponse.SC_OK);
                        } else {
                            j.addProperty("Error", "Description is EMPTY or exceeds maximum allowable length");
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        j.addProperty("Error", "Access Denied. Task belongs to a different user.");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    j.addProperty("Error", "Use correct ID.");
                }
            } else {
                j.addProperty("Error", "Invalid User Credentials");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (IllegalStateException e) {
            j.addProperty("Exception", e.toString());

        } catch (Exception e) {
            j.addProperty("Exception", e.toString());
        }
        return j.toString();
    }

    /**
     * @param request
     * @param id
     * @param file
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tasks/{id}/attachments", method = RequestMethod.POST)
    public @ResponseBody
    String handleFileUpload(HttpServletRequest request, @PathVariable("id") long id, @RequestParam(value = "file") MultipartFile file, HttpServletResponse response) throws Exception {


        JsonObject jObj = new JsonObject();
        try {
            User user = authenticateUser(request);
            if (file != null) {
                if (user != null) {
                    Task task = taskDao.findByTaskId(id);
                    if (task != null) {
                        if (user == task.getUser()) {
                            MediaFile mediaFile = new MediaFile();
                            MultipartFile fileInMemory = file;
                            String fileName = fileInMemory.getOriginalFilename();
                            String baseName = "/home/ritesh/Documents/";
                            File userFile = new File(baseName + task.getUser().getEmailId());
                            String currentTime = String.valueOf(System.currentTimeMillis());
                            if (!userFile.exists()) {
                                if (userFile.mkdir()) {
                                    File eventFile = new File(userFile + "/" + currentTime);
                                    eventFile.mkdir();
                                    mediaFile.setFileName(eventFile.getPath());
                                }
                            } else {
                                File eventFile = new File(userFile + "/" + currentTime);
                                if (!eventFile.exists()) {
                                    if (eventFile.mkdir()) {
                                        mediaFile.setFileName(eventFile.getPath());
                                    }
                                }
                            }
                            File localFile = new File(baseName + task.getUser().getEmailId() + "/" + currentTime + "/", fileName);
                            fileInMemory.transferTo(localFile);
                            mediaFile.setFileName(localFile.getPath());
                            mediaFile.setTask(task);
                            fileUploadDao.save(mediaFile);
                            jObj.addProperty("message", "File Location Saved");
                        } else {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            jObj.addProperty("Error", "Access Denied. Task belongs to a different user.");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        jObj.addProperty("Error", "Use correct ID.");
                    }
                } else {
                    jObj.addProperty("Error", "Invalid User Credentials");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                jObj.addProperty("Error", "File not selected");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IllegalStateException e) {
            jObj.addProperty("message", e.toString());

        } catch (Exception e) {
            jObj.addProperty("message", e.toString());
        }
        return jObj.toString();
    }

    /**
     * @param idAttachments
     * @param response
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tasks/{id}/attachments/{idAttachments}", method = RequestMethod.DELETE, produces = "application/json")
    public @ResponseBody
    String deleteMediaFile(@PathVariable("idAttachments") long idAttachments, @PathVariable("id") long id, HttpServletResponse response, HttpServletRequest request) throws Exception {

        JsonObject j = new JsonObject();
        try {
            User user = authenticateUser(request);
            if (user != null) {
                MediaFile mediaFile = fileUploadDao.findByFileId(idAttachments);
                if (mediaFile != null) {
                    Task task = taskDao.findByTaskId(id);
                    if ((task != null) && (task == mediaFile.getTask())) {
                        if (user == mediaFile.getTask().getUser()) {
                            fileUploadDao.delete(mediaFile);
                            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                            j.addProperty("message", "File deleted");
                        } else {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            j.addProperty("Error", "Access Denied. Task belongs to a different user.");
                        }
                    } else {
                        j.addProperty("Error", "Task not found Or Attachment/File does not belong to this file.");
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    j.addProperty("Error", "Use correct File ID.");
                }
            } else {
                j.addProperty("Error", "Invalid User Credentials");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (IllegalStateException e) {
            j.addProperty("Exception", e.toString());

        } catch (Exception e) {
            j.addProperty("Exception", e.toString());
        }
        return j.toString();
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.GET, produces = "application/json")
    protected @ResponseBody
    String getUserTask(HttpServletRequest request, HttpServletResponse response) throws Exception {

        JsonObject j = new JsonObject();
        try {
            User user = authenticateUser(request);
            if (user != null) {
                List<Task> tasks = user.getTasks();
                if (tasks != null && tasks.size() > 0 && !tasks.isEmpty()) {
                    for (int i = 0; i < tasks.size(); i++) {
                        Task task = tasks.get(i);
                        j.addProperty("Task Id:" + task.getTaskId(), "Description: " + task.getDescription());
                        List<MediaFile> files = task.getMediaFiles();
                        if (files != null && files.size() > 0 && !files.isEmpty()) {
                            for (int k = 0; k < files.size(); k++) {
                                MediaFile file = files.get(k);
                                j.addProperty("File Id:" + file.getFileId(), "File Name: " + file.getFileName());
                            }
                        } else {
                            j.addProperty("Error", "No attachments added for this task");
                        }
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    j.addProperty("Error", "No Tasks added for this user");
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
            } else {
                j.addProperty("Error", "Invalid User Credentials");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (IllegalStateException e) {
            j.addProperty("Exception", e.toString());

        } catch (Exception e) {
            j.addProperty("Exception", e.toString());
        }
        return j.toString();
    }

    /**
     * @param id
     * @param response
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tasks/{id}/attachments", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getMediaFiles(@PathVariable("id") long id, HttpServletResponse response, HttpServletRequest request) throws Exception {

        JsonObject j = new JsonObject();
        try {
            User user = authenticateUser(request);
            if (user != null) {
                Task task = taskDao.findByTaskId(id);
                if ((task != null)) {
                    if (user == task.getUser()) {
                        List<MediaFile> files = task.getMediaFiles();
                        if (!files.isEmpty()) {
                            for (int i = 0; i < files.size(); i++) {
                                j.addProperty("File ID" + files.get(i).getFileId(), files.get(i).getFileName());
                            }
                            response.setStatus(HttpServletResponse.SC_OK);
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            j.addProperty("Error", "This task has no files attached to it.");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        j.addProperty("Error", "Access Denied. Task belongs to a different user.");
                    }
                } else {
                    j.addProperty("Error", "Task not found.");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                j.addProperty("Error", "Invalid User Credentials");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (IllegalStateException e) {
            j.addProperty("Exception", e.toString());

        } catch (Exception e) {
            j.addProperty("Exception", e.toString());
        }
        return j.toString();
    }

    /**
     * @param request
     * @param id
     * @param file
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tasks/{id}/s3attachments", method = RequestMethod.POST)
    public @ResponseBody
    String handleS3FileUpload(HttpServletRequest request, @PathVariable("id") long id, @RequestParam(value = "file") MultipartFile file, HttpServletResponse response) throws Exception {


        JsonObject jObj = new JsonObject();
        try {
            User user = authenticateUser(request);
            if (file != null) {
                if (user != null) {
                    Task task = taskDao.findByTaskId(id);
                    if (task != null) {
                        if (user == task.getUser()) {
                            MediaFile mediaFile = fileArchiveService.saveFileToS3(file);
                            mediaFile.setTask(task);
                            fileUploadDao.save(mediaFile);
                            jObj.addProperty("message", "File Location Saved");
                        } else {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            jObj.addProperty("Error", "Access Denied. Task belongs to a different user.");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        jObj.addProperty("Error", "Use correct ID.");
                    }
                } else {
                    jObj.addProperty("Error", "Invalid User Credentials");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                jObj.addProperty("Error", "File not selected");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IllegalStateException e) {
            jObj.addProperty("message", e.toString());

        } catch (Exception e) {
            jObj.addProperty("message", e.toString());
        }
        return jObj.toString();
    }

    /**
     * @param idAttachments
     * @param id
     * @param response
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/tasks/{id}/s3attachments/{idAttachments}", method = RequestMethod.DELETE, produces = "application/json")
    public @ResponseBody
    String deleteS3MediaFile(@PathVariable("idAttachments") long idAttachments, @PathVariable("id") long id, HttpServletResponse response, HttpServletRequest request) throws Exception {

        JsonObject j = new JsonObject();
        try {
            User user = authenticateUser(request);
            if (user != null) {
                MediaFile mediaFile = fileUploadDao.findByFileId(idAttachments);
                if (mediaFile != null) {
                    Task task = taskDao.findByTaskId(id);
                    if ((task != null) && (task == mediaFile.getTask())) {
                        if (user == mediaFile.getTask().getUser()) {
                            fileArchiveService.deleteFileFromS3(mediaFile);
                            fileUploadDao.delete(mediaFile);
                            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                            j.addProperty("message", "File deleted");
                        } else {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            j.addProperty("Error", "Access Denied. Task belongs to a different user.");
                        }
                    } else {
                        j.addProperty("Error", "Task not found Or Attachment/File does not belong to this file.");
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    j.addProperty("Error", "Use correct File ID.");
                }
            } else {
                j.addProperty("Error", "Invalid User Credentials");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (IllegalStateException e) {
            j.addProperty("Exception", e.toString());

        } catch (Exception e) {
            j.addProperty("Exception", e.toString());
        }
        return j.toString();
    }


    /**
     * Private class to authenticate user credentials. This class does not determine if the user is
     * trying to access the tasks and files that do not belong to him,
     * that verification is done inside the corresponding function.
     *
     * @param request
     * @return
     */
    private User authenticateUser(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            BCryptPasswordEncoder decodePassword = new BCryptPasswordEncoder();
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                    Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            User user = userDao.findUserByEmailId(values[0]);
            if (user != null) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                boolean flag = passwordEncoder.matches(values[1], user.getPassword());
                if (flag) {
                    return user;
                } else {
                    user = null;
                    return user;
                }
            } else {
                return user;
            }
        }
        return null;
    }

}
