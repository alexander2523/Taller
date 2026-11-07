package mx.taller.auth;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UsuarioRepository extends JpaRepository<Usuario,UUID>{Optional<Usuario> findByEmailAndActivoTrue(String email); boolean existsByEmailIgnoreCase(String email); java.util.List<Usuario> findByRolIsNullAndActivoTrue(); org.springframework.data.domain.Page<Usuario> findByActivoTrue(org.springframework.data.domain.Pageable pageable);}
