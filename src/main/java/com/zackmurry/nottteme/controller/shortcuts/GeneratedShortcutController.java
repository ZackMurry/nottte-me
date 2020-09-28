package com.zackmurry.nottteme.controller.shortcuts;

import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.GeneratedShortcut;
import com.zackmurry.nottteme.services.ShortcutService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class GeneratedShortcutController {

    @Autowired
    private ShortcutService shortcutService;


    @GetMapping("/principal/preferences/shortcuts/generated")
    public List<GeneratedShortcut> getGeneratedShortcutsOfPrincipal() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return shortcutService.getGeneratedShortcutsByUser(username);
    }

    //todo add @NotNulls to a lot of things (along with other constraints)
    @PostMapping("/principal/preferences/shortcuts/generated")
    public String addGeneratedShortcutToPrincipal(@RequestBody @NotNull CSSAttribute cssAttribute) throws NotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return shortcutService.addGeneratedShortcutToUser(username, cssAttribute);
    }

}
