package com.exemplo.crud;

import com.exemplo.crud.model.Cliente;
import com.exemplo.crud.repository.ClienteRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClienteSeleniumIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ClienteRepository clienteRepository;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port + "/index.html";
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Teste 1 (Insert): Validar inserção de dados através da interface e confirmação no banco de dados")
    void teste1InsertValidarInsercaoEConfirmacaoNoBanco() {
        // Limpar registro com e-mail do teste se já existir
        clienteRepository.findByEmail("joao.silva@teste.com").ifPresent(clienteRepository::delete);

        // Acessa a interface web
        driver.get(baseUrl);

        // Preenche os campos do formulário
        WebElement inputNome = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));
        WebElement inputEmail = driver.findElement(By.id("email"));
        WebElement inputTelefone = driver.findElement(By.id("telefone"));
        WebElement btnSalvar = driver.findElement(By.id("btn-salvar"));

        inputNome.clear();
        inputNome.sendKeys("João Silva");

        inputEmail.clear();
        inputEmail.sendKeys("joao.silva@teste.com");

        inputTelefone.clear();
        inputTelefone.sendKeys("11999998888");

        // Clica no botão de salvar
        btnSalvar.click();

        // Aguarda a confirmação visual na tabela
        WebElement emailNaTabela = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[contains(@class, 'col-email') and text()='joao.silva@teste.com']")
        ));
        assertThat(emailNaTabela.isDisplayed()).isTrue();

        // Validação no Banco de Dados (PostgreSQL via JPA)
        Optional<Cliente> clienteNoBanco = clienteRepository.findByEmail("joao.silva@teste.com");
        assertThat(clienteNoBanco).isPresent();
        assertThat(clienteNoBanco.get().getNome()).isEqualTo("João Silva");
        assertThat(clienteNoBanco.get().getEmail()).isEqualTo("joao.silva@teste.com");
        assertThat(clienteNoBanco.get().getTelefone()).isEqualTo("11999998888");
    }

    @Test
    @DisplayName("Teste 2 (Update): Validar a atualização de dados através da interface e confirmação no banco de dados")
    void teste2UpdateValidarAtualizacaoEConfirmacaoNoBanco() {
        // Prepara um cliente prévio no banco de dados
        clienteRepository.findByEmail("mariana.original@teste.com").ifPresent(clienteRepository::delete);
        Cliente clienteSalvo = clienteRepository.save(new Cliente("Mariana Ramos", "mariana.original@teste.com", "11888887777"));
        Long id = clienteSalvo.getId();

        // Acessa a interface web
        driver.get(baseUrl);

        // Aguarda o botão Editar do cliente estar visível e clica nele
        WebElement btnEditar = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-editar-" + id)));
        btnEditar.click();

        // Valida que os campos foram carregados no formulário
        WebElement inputNome = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));
        WebElement inputTelefone = driver.findElement(By.id("telefone"));
        WebElement btnSalvar = driver.findElement(By.id("btn-salvar"));

        wait.until(ExpectedConditions.textToBePresentInElementValue(inputNome, "Mariana Ramos"));

        // Modifica os dados
        inputNome.clear();
        inputNome.sendKeys("Mariana Ramos Atualizada");

        inputTelefone.clear();
        inputTelefone.sendKeys("11977776666");

        // Clica em Salvar (Atualizar)
        btnSalvar.click();

        // Aguarda a linha ser atualizada na tabela
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//tr[@id='cliente-row-" + id + "']//td[contains(@class, 'col-nome')]"),
                "Mariana Ramos Atualizada"
        ));

        // Validação no Banco de Dados
        Cliente atualizadoNoBanco = clienteRepository.findById(id).orElseThrow();
        assertThat(atualizadoNoBanco.getNome()).isEqualTo("Mariana Ramos Atualizada");
        assertThat(atualizadoNoBanco.getEmail()).isEqualTo("mariana.original@teste.com");
        assertThat(atualizadoNoBanco.getTelefone()).isEqualTo("11977776666");
    }

    @Test
    @DisplayName("Teste 3 (Delete): Validar a exclusão de dados através da interface e confirmação no banco de dados")
    void teste3DeleteValidarExclusaoEConfirmacaoNoBanco() {
        // Prepara um cliente prévio no banco de dados
        clienteRepository.findByEmail("roberto@teste.com").ifPresent(clienteRepository::delete);
        Cliente clienteSalvo = clienteRepository.save(new Cliente("Roberto Santos", "roberto@teste.com", "11777775555"));
        Long id = clienteSalvo.getId();

        // Acessa a interface web
        driver.get(baseUrl);

        // Localiza e clica no botão de excluir
        WebElement btnExcluir = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-excluir-" + id)));
        btnExcluir.click();

        // Confirma o diálogo nativo do navegador (window.confirm)
        wait.until(ExpectedConditions.alertIsPresent());
        Alert confirmAlert = driver.switchTo().alert();
        confirmAlert.accept();

        // Aguarda a linha desaparecer da interface
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("cliente-row-" + id)));

        // Validação no Banco de Dados: o registro não deve mais existir
        Optional<Cliente> clienteExcluido = clienteRepository.findById(id);
        assertThat(clienteExcluido).isEmpty();
    }
}

