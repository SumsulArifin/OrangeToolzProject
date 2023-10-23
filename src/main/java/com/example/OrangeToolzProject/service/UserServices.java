package com.example.OrangeToolzProject.service;

import com.example.OrangeToolzProject.entity.User;
import com.example.OrangeToolzProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserServices {
    @Autowired
    UserRepository userRepository;

//    public MessageResponse uploadCSV(MultipartFile file) {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            List<String> errorMessages = new ArrayList<>();
//
//            reader.lines().skip(1).forEach(line -> {
//                String[] data = line.split(",");
//                if (data.length >= 2) {
//                    User user = new User();
//                    try {
//                        // Set outlet properties from CSV data
//                        user.setName(data[0]);
//                        user.setMobile(data[1]);
//                        userRepository.save(user);
//
//                    } catch (Exception e) {
//                        errorMessages.add("Error processing line: " + line + ". Reason: " + e.getMessage());
//                    }
//                } else {
//                    errorMessages.add("Invalid data at line: " + line);
//                }
//            });
//
//            if (errorMessages.isEmpty()) {
//                return new MessageResponse(Message.SUCCESS_CSV_UPLOAD);
//            } else {
//                StringBuilder errorMessage = new StringBuilder("Errors occurred while processing CSV file:\n");
//                for (String error : errorMessages) {
//                    errorMessage.append(error).append("\n");
//                }
//                return new MessageResponse(errorMessage.toString());
//            }
//
//        } catch (Exception e) {
//            return new MessageResponse("Error while processing CSV file: " + e.getMessage());
//        }
//    }




//    public MessageResponse uploadCSV(MultipartFile file) {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            List<String> errorMessages = new ArrayList<>();
//            List<User> userList = new ArrayList<>();
//
//            reader.lines().skip(1).forEach(line -> {
//                String[] data = line.split(",");
//                if (data.length >= 2) {
//                    User user = new User();
//                    try {
//                        // Set outlet properties from CSV data
//                        user.setName(data[0]);
//                        user.setMobile(data[1]);
//                       // userRepository.save(user);
//
//                        userList.add(user);
//
//                    } catch (Exception e) {
//                        errorMessages.add("Error processing line: " + line + ". Reason: " + e.getMessage());
//                    }
//                } else {
//                    errorMessages.add("Invalid data at line: " + line);
//                }
//            });
//
//            if (errorMessages.isEmpty()) {
//                userRepository.saveAll(userList);
//                return new MessageResponse(Message.SUCCESS_CSV_UPLOAD);
//            } else {
//                StringBuilder errorMessage = new StringBuilder("Errors occurred while processing CSV file:\n");
//                for (String error : errorMessages) {
//                    errorMessage.append(error).append("\n");
//                }
//                return new MessageResponse(errorMessage.toString());
//            }
//
//        } catch (Exception e) {
//            return new MessageResponse("Error while processing CSV file: " + e.getMessage());
//        }
//    }


//    private AtomicInteger successfulUploads = new AtomicInteger(0);
//    private AtomicInteger failedUploads = new AtomicInteger(0);
//
//    public MessageResponse uploadCSV(MultipartFile file) {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            int threadCount = 100000; // Adjust the number of threads based on your server's capabilities
//            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//            executor.setCorePoolSize(threadCount);
//            executor.setMaxPoolSize(threadCount);
//            executor.initialize();
//
//            String line;
//            reader.readLine(); // Skip the header line
//
//            while ((line = reader.readLine()) != null) {
//                if (!line.isEmpty()) {
//                    String finalLine = line;
//                    executor.submit(() -> processCSVLine(finalLine));
//                }
//            }
//
//            // Wait for all threads to complete
//            executor.getThreadPoolExecutor().shutdown();
//            while (!executor.getThreadPoolExecutor().isTerminated()) {
//            }
//
//            return new MessageResponse("Successfully processed " + successfulUploads.get() +
//                    " records, with " + failedUploads.get() + " failures.");
//        } catch (Exception e) {
//            return new MessageResponse("Error processing CSV file: " + e.getMessage());
//        }
//    }
//
//    private void processCSVLine(String line) {
//        try {
//            String[] data = line.split(",");
//            if (data.length >= 2) {
//                User user = new User();
//                user.setName(data[0]);
//                user.setMobile(data[1]);
//                userRepository.save(user);
//                successfulUploads.incrementAndGet();
//            } else {
//                failedUploads.incrementAndGet();
//            }
//        } catch (Exception e) {
//            failedUploads.incrementAndGet();
//        }
//    }
//


////////////////////
private AtomicInteger successfulUploads = new AtomicInteger(0);
    private AtomicInteger failedUploads = new AtomicInteger(0);

    public MessageResponse uploadCSV(MultipartFile file) {
        int threadCount = 10; // Number of threads to use for parallel processing

        Executor executor = new TaskExecutorAdapter(threadCount);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            reader.readLine(); // Skip the header line

            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    String finalLine = line;
                    executor.execute(() -> processCSVLine(finalLine));
                }
            }

            // Wait for all threads to complete
            while (successfulUploads.get() + failedUploads.get() < 10_000_000) {
                Thread.sleep(1000); // Adjust the polling interval as needed
            }

            return new MessageResponse("Successfully processed " + successfulUploads.get() +
                    " records, with " + failedUploads.get() + " failures.");
        } catch (Exception e) {
            return new MessageResponse("Error processing CSV file: " + e.getMessage());
        }
    }

    private void processCSVLine(String line) {
        try {
            String[] data = line.split(",");
            if (data.length >= 2) {
                User user = new User();
                user.setName(data[0]);
                user.setMobile(data[1]);
                userRepository.save(user);
                successfulUploads.incrementAndGet();
            } else {
                failedUploads.incrementAndGet();
            }
        } catch (Exception e) {
            failedUploads.incrementAndGet();
        }
    }

    private class TaskExecutorAdapter implements Executor {
        private final int threadCount;

        public TaskExecutorAdapter(int threadCount) {
            this.threadCount = threadCount;
        }

        @Override
        public void execute(Runnable command) {
            new Thread(command).start();
        }
    }


}
