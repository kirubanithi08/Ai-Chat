package com.example.Ai_ChatBot.Common;



public record ApiResponse<T>(boolean success, T data, String message) {}

