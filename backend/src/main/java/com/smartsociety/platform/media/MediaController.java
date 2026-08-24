package com.smartsociety.platform.media;
import java.nio.file.NoSuchFileException;
import com.smartsociety.platform.common.DomainException;
import com.smartsociety.platform.complaint.*;
import com.smartsociety.platform.security.SecurityUser;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/complaints/{complaintId}/photos")
public class MediaController {
    final ComplaintRepository complaints; final ComplaintPhotoRepository photos; final MediaStorage storage;
    public MediaController(ComplaintRepository c,ComplaintPhotoRepository p,MediaStorage s){complaints=c;photos=p;storage=s;}

    @PostMapping public Map<String,String> upload(@PathVariable UUID complaintId,@RequestParam MultipartFile file,@AuthenticationPrincipal SecurityUser u)throws Exception{
        var c=complaints.findById(complaintId).orElseThrow(()->new DomainException(HttpStatus.NOT_FOUND,"Complaint not found")); authorize(c,u);
        if(file.isEmpty()||file.getSize()>5*1024*1024)throw new DomainException(HttpStatus.BAD_REQUEST,"Image must be non-empty and <= 5MB");
        byte[] bytes=file.getBytes(); String detected=detect(bytes);
        if(detected==null)throw new DomainException(HttpStatus.BAD_REQUEST,"Unsupported or invalid image");
        var key=storage.store(new ByteArrayInputStream(bytes),Objects.requireNonNull(file.getOriginalFilename()),detected,bytes.length);
        var photo=new ComplaintPhoto();photo.setComplaint(c);photo.setUploadedBy(u.user());photo.setStorageKey(key);photo.setOriginalFilename(Objects.requireNonNull(file.getOriginalFilename()));photo.setMimeType(detected);photo.setFileSize(bytes.length);photos.save(photo);
        return Map.of("id",photo.getId().toString(),"storageKey",key);
    }

    @GetMapping("/{photoId}") public ResponseEntity<InputStreamResource> read(@PathVariable UUID complaintId,@PathVariable UUID photoId,@AuthenticationPrincipal SecurityUser u)throws IOException{
        var c=complaints.findById(complaintId).orElseThrow(()->new DomainException(HttpStatus.NOT_FOUND,"Complaint not found")); authorize(c,u);
        var photo=photos.findByIdAndComplaintId(photoId,complaintId).orElseThrow(()->new DomainException(HttpStatus.NOT_FOUND,"Photo not found"));
        MediaStorage.StoredMedia media; try{media=storage.read(photo.getStorageKey());}catch(FileNotFoundException|NoSuchFileException e){throw new DomainException(HttpStatus.NOT_FOUND,"Photo file not found");}
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(photo.getMimeType())).contentLength(media.contentLength()).cacheControl(CacheControl.noCache()).body(new InputStreamResource(media.input()));
    }
    private void authorize(Complaint c,SecurityUser u){if(u.user().getRole()!=com.smartsociety.platform.common.Enums.Role.ADMIN&&!c.getResident().getId().equals(u.id()))throw new DomainException(HttpStatus.FORBIDDEN,"Not allowed");}
    private String detect(byte[] b){
        if(b.length>=3&&(b[0]&255)==0xFF&&(b[1]&255)==0xD8&&(b[2]&255)==0xFF)return "image/jpeg";
        if(b.length>=8&&(b[0]&255)==0x89&&b[1]==0x50&&b[2]==0x4E&&b[3]==0x47&&b[4]==0x0D&&b[5]==0x0A&&b[6]==0x1A&&b[7]==0x0A)return "image/png";
        if(b.length>=12&&b[0]=='R'&&b[1]=='I'&&b[2]=='F'&&b[3]=='F'&&b[8]=='W'&&b[9]=='E'&&b[10]=='B'&&b[11]=='P')return "image/webp";
        return null;
    }
}
