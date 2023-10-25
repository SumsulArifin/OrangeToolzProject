package com.example.OrangeToolzProject.service;

import com.example.OrangeToolzProject.entity.User;
import com.example.OrangeToolzProject.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class UserServicesImpl implements UserServices{

    @Autowired
    UserRepository userRepository;
    private AtomicInteger successfulUploads = new AtomicInteger(0);
    private AtomicInteger failedUploads = new AtomicInteger(0);
    @Override
    @Transactional
    public MessageResponse uploadCSV(MultipartFile file) {
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            reader.readLine();

            List<CompletableFuture<Void>> futures = reader.lines()
                    .filter(line -> !line.isEmpty())
                    .map(this::processCSVLineAsync)
                    .collect(Collectors.toList());
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();

            return new MessageResponse("Successfully processed " + successfulUploads.get() +
                    " records, with " + failedUploads.get() + " failures.");
        } catch (Exception e) {
            return new MessageResponse("Error processing CSV file: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    private CompletableFuture<Void> processCSVLineAsync(String line) {
        return CompletableFuture.runAsync(() -> {
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
        });
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
