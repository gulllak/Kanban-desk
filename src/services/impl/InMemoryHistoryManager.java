package services.impl;

import models.Node;
import models.Task;
import services.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList history = new CustomLinkedList();

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        history.removeNode(history.fastNodeSearch.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    public static class CustomLinkedList {
        Node first;
        Node last;
        Map<Integer, Node> fastNodeSearch = new HashMap<>();

        public void linkLast(Task task) {
            if(fastNodeSearch.containsKey(task.getId())) {
                removeNode(fastNodeSearch.get(task.getId()));
            }

            Node curr = new Node(task);
            if (last != null) {
                last.next = curr;
                curr.prev = last;
            } else {
                first = curr;
            }
            last = curr;

            fastNodeSearch.put(task.getId(), curr);
        }

        public ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            Node current = first;
            while (current != null) {
                tasks.add(current.getTask());
                current = current.next;
            }
            return tasks;
        }

        public void removeNode(Node node) {
            if(node == first & node == last) {
                last = null;
                first = null;
                node.next = null;
                node.prev = null;
            } else if (node == first) {
                first = node.next;
                node.next.prev = null;
                node.next = null;
                node.prev = null;
            } else if(node == last) {
                last = node.prev;
                node.prev.next = null;
                node.prev = null;
                node.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
                node.next = null;
                node.prev = null;
            }

            fastNodeSearch.remove(node.getTask().getId());
        }
    }
}
