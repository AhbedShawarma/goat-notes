package com.example.syncservice.folder

import org.springframework.web.bind.annotation.*


@RestController
class FolderResource(val service: FolderService) {
    @GetMapping(path= ["/folders"])
    fun getNotes(): List<Folder> = service.getFolders()

    @PostMapping(path= ["/folders"])
    fun post(@RequestBody folder: Folder) = service.storeFolder(folder)

    @DeleteMapping(path= ["/folders"])
    fun delete() = service.deleteAllFolders()
}