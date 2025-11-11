package com.practicum.playlistmaker.data.history

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
}