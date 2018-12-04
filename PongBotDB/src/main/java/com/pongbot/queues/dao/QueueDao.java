package com.pongbot.queues.dao;

import java.util.List;

import com.pongbot.queues.models.Task;

public interface QueueDao {
  void put(Task task);
  void delete(Task task);
  List<Task> get(int count);
}
