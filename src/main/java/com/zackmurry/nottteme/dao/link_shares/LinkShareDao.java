package com.zackmurry.nottteme.dao.link_shares;

import com.zackmurry.nottteme.models.LinkShare;
import com.zackmurry.nottteme.models.LinkShareRequest;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;
import java.util.UUID;

public interface LinkShareDao {

    HttpStatus createShareableLink(LinkShareRequest request);

    LinkShare getLinkShareById(UUID id) throws NotFoundException, SQLException;

    HttpStatus incrementShareableLinkUsages(UUID id);

}
