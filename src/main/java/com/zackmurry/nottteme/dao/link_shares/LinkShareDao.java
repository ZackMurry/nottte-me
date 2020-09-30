package com.zackmurry.nottteme.dao.link_shares;

import com.zackmurry.nottteme.models.sharing.LinkShare;
import com.zackmurry.nottteme.models.sharing.LinkShareRequest;
import com.zackmurry.nottteme.models.sharing.LinkShareStatus;
import com.zackmurry.nottteme.models.sharing.ShareAuthority;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkShareDao {

    HttpStatus createShareableLink(LinkShareRequest request);

    LinkShare getLinkShareById(UUID id) throws NotFoundException, SQLException;

    HttpStatus incrementShareableLinkUsages(UUID id);

    String getAuthorById(UUID id) throws NotFoundException, SQLException;

    HttpStatus setShareableLinkStatus(UUID id, LinkShareStatus status);

    boolean shareableLinkExists(long noteId);

    Optional<LinkShare> getLinkShare(long noteId, ShareAuthority authority);

    Optional<LinkShare> getLinkShare(long noteId);

    List<LinkShare> getLinkSharesByNoteId(long noteId);

    HttpStatus setStatusOfLinkSharesOfNote(long noteId, LinkShareStatus newStatus);

    HttpStatus setStatusOfLinkSharesByUser(String username, LinkShareStatus newStatus);

    HttpStatus updateSharableLink(UUID id, LinkShare newLinkShare);

}
