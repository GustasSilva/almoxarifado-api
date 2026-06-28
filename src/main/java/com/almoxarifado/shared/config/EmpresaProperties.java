package com.almoxarifado.shared.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "empresa")
@Getter
@Setter
public class EmpresaProperties {
    private String razaoSocial;
    private String cnpj;
    private String endereco;
    private String telefone;
    private String email;
}
