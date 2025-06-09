package com.crmtech360.crmtech360_backend.service.impl;

import com.crmtech360.crmtech360_backend.entity.Usuario;
import com.crmtech360.crmtech360_backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    // Eliminado: private final RolPermisoRepository rolPermisoRepository;

    @Autowired
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        // Eliminado: this.rolPermisoRepository = rolPermisoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Intentando cargar usuario por nombre de usuario: {}", username);

        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con nombre de usuario: {}", username);
                    return new UsernameNotFoundException("Usuario no encontrado con nombre de usuario: " + username);
                });

        log.info("Usuario encontrado: {}, Rol: {}, Habilitado: {}",
                usuario.getNombreUsuario(), usuario.getRolUsuario(), usuario.isHabilitado());

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Añadir el ROL principal del usuario como una autoridad.
        // Ahora solo nos basamos en el String 'rolUsuario' de la entidad Usuario.
        if (usuario.getRolUsuario() != null && !usuario.getRolUsuario().trim().isEmpty()) {
            // Es una buena práctica prefijar los roles con "ROLE_" para Spring Security
            String roleName = "ROLE_" + usuario.getRolUsuario().toUpperCase().replace(" ", "_");
            authorities.add(new SimpleGrantedAuthority(roleName));
            log.debug("Rol principal añadido como autoridad: {}", roleName);
        } else {
            log.warn("El usuario '{}' no tiene un rol principal asignado o está vacío.", username);
        }

        // Eliminada la lógica para buscar y añadir PERMISOS específicos de las tablas eliminadas.
        // El usuario solo tendrá el rol derivado de su campo 'rolUsuario'.

        if(authorities.isEmpty()){
            log.warn("El usuario '{}' no tiene roles asignados. La autenticación podría funcionar pero la autorización fallará para recursos protegidos.", username);
        }

        // Usamos el estado 'habilitado' del usuario de la base de datos
        return new User(
                usuario.getNombreUsuario(),
                usuario.getContrasena(),
                usuario.isHabilitado(), // Usar el valor del campo 'habilitado' de la entidad Usuario
                true, // accountNonExpired (Considerar añadir un campo si necesitas expiración de cuenta)
                true, // credentialsNonExpired (Considerar añadir un campo si necesitas expiración de credenciales)
                true, // accountNonLocked (Considerar añadir un campo si necesitas bloqueo de cuenta)
                authorities
        );
    }
}