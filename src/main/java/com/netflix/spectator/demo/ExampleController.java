/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.spectator.demo;

import com.netflix.spectator.api.Registry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample API: health, items, and user lookup. Records request metrics with method and path,
 * and per-user request count keyed by userId from the path.
 */
@RestController
@RequestMapping("/api")
public class ExampleController {

  private final Registry registry;

  public ExampleController(Registry registry) {
    this.registry = registry;
  }

  @GetMapping("/health")
  public ResponseEntity<Map<String, String>> health() {
    recordRequest("GET", "/api/health");
    return ResponseEntity.ok(Collections.singletonMap("status", "UP"));
  }

  @GetMapping("/items")
  public ResponseEntity<Map<String, Object>> listItems() {
    recordRequest("GET", "/api/items");
    Map<String, Object> body = new HashMap<>();
    body.put("items", Collections.emptyList());
    return ResponseEntity.ok(body);
  }

  @GetMapping("/users/{userId}")
  public ResponseEntity<Map<String, String>> getUser(@PathVariable String userId) {
    recordRequest("GET", "/api/users/" + userId);
    registry.counter("user.requests", "userId", userId).increment();
    registry.counter("requests.by.user." + userId).increment();
    return ResponseEntity.ok(Collections.singletonMap("userId", userId));
  }

  private void recordRequest(String method, String path) {
    registry.counter("server.requests", "method", method, "path", path).increment();
  }
}
