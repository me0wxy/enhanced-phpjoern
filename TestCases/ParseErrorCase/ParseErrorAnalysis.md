# CMS New Features Parse Error Results

|                               | **Parsing Error files**     |             |                            |             |
| ----------------------------- | --------------------------- | ----------- | -------------------------- | ----------- |
| **PHPJoern Version**          | **PHP 7.0**                 | **PHP 7.4** | **PHP 7.0**                | **PHP 7.4** |
| **CMS**  **Source**  **Code** | **Before CMS Installation** |             | **After CMS Installation** |             |
| **symfony-5.2.7**             | **851**                     | **0**       | **Install failed**         |             |
| **WordPress-5.7.1**           | **0**                       | **0**       | **0**                      | **0**       |
| **october-2.0.10**            | **0**                       | **0**       | **607**                    | **4**       |
| **core-10.7.0**               | **54**                      | **0**       | **295**                    | **4**       |
| **prestashop_1.7.6.5**        | **0**                       | **0**       | **9**                      | **6**       |
| **kanboard-1.2.19**           | **0**                       | **0**       | **7**                      | **0**       |
| **phpmyadmin-RELEASE_5_1_0**  | **0**                       | **0**       | **1188**                   | **2**       |
| **roundcubemail-1.4.11**      | **0**                       | **0**       | **13**                     | **1**       |
| **MISP-2.4.142**              | **0**                       | **0**       | **Install failed**         |             |

## PHPMyAdmin

安装后的CMS存在2个解析错误。

### PHP 8.0 New Features

```
[ERROR] In phpmyadmin-RELEASE_5_1_0/vendor/symfony/polyfill-mbstring/bootstrap80.php: syntax error, unexpected '|', expecting variable (T_VARIABLE)
```

### Intentional Syntax Mistakes

```
[ERROR] In phpmyadmin-RELEASE_5_1_0/vendor/phpunit/php-code-coverage/tests/_files/Crash.php: syntax error, unexpected '{', expecting identifier (T_STRING)
```

Result (PHP 8.0 New Features : Intentional Syntax Mistakes) : `1:1`。

## October

4个解析错误全部都是PHP 8.0的新特性导致的。

### PHP 8.0 New Features

```
[ERROR] In october-2.0.10/vendor/symfony/polyfill-intl-normalizer/bootstrap80.php: syntax error, unexpected '|', expecting '{'
[ERROR] In october-2.0.10/vendor/symfony/polyfill-mbstring/bootstrap80.php: syntax error, unexpected '|', expecting variable (T_VARIABLE)
[ERROR] In october-2.0.10/vendor/symfony/polyfill-intl-idn/bootstrap80.php: syntax error, unexpected '|', expecting '{'
[ERROR] In october-2.0.10/vendor/symfony/polyfill-iconv/bootstrap80.php: syntax error, unexpected '|', expecting '{'
```

Result (PHP 8.0 New Features : Intentional Syntax Mistakes) : `4:0`。

## Core

4个解析错误全部都是PHP 8.0的新特性导致的。

### PHP 8.0 New Features

```
[ERROR] In core-10.7.0/lib/composer/symfony/polyfill-intl-normalizer/bootstrap80.php: syntax error, unexpected '|', expecting '{'
[ERROR] In core-10.7.0/lib/composer/symfony/polyfill-mbstring/bootstrap80.php: syntax error, unexpected '|', expecting variable (T_VARIABLE)
[ERROR] In core-10.7.0/lib/composer/symfony/polyfill-intl-idn/bootstrap80.php: syntax error, unexpected '|', expecting '{'
[ERROR] In core-10.7.0/lib/composer/symfony/polyfill-iconv/bootstrap80.php: syntax error, unexpected '|', expecting '{'
```

Result (PHP 8.0 New Features : Intentional Syntax Mistakes) : `4:0`。

## PrestaShop

6个文件解析错误。

### PHP 8.0 New Features

```
[ERROR] In prestashop_1.7.6.5/modules/ps_accounts/vendor/symfony/dependency-injection/Tests/Fixtures/includes/uniontype_classes.php: syntax error, unexpected '|', expecting variable (T_VARIABLE)
[ERROR] In prestashop_1.7.6.5/modules/ps_metrics/vendor/symfony/dependency-injection/Tests/Fixtures/includes/uniontype_classes.php: syntax error, unexpected '|', expecting variable (T_VARIABLE)
[ERROR] In prestashop_1.7.6.5/modules/ps_eventbus/vendor/symfony/dependency-injection/Tests/Fixtures/includes/uniontype_classes.php: syntax error, unexpected '|', expecting variable (T_VARIABLE)
```

### Intentional Syntax Mistakes

```
[ERROR] In prestashop_1.7.6.5/modules/ps_accounts/vendor/symfony/config/Tests/Fixtures/ParseError.php: syntax error, unexpected end of file, expecting function (T_FUNCTION) or const (T_CONST)
[ERROR] In prestashop_1.7.6.5/modules/ps_metrics/vendor/symfony/config/Tests/Fixtures/ParseError.php: syntax error, unexpected end of file, expecting function (T_FUNCTION) or const (T_CONST)
[ERROR] In prestashop_1.7.6.5/modules/ps_eventbus/vendor/symfony/config/Tests/Fixtures/ParseError.php: syntax error, unexpected end of file, expecting function (T_FUNCTION) or const (T_CONST)
```

Result (PHP 8.0 New Features : Intentional Syntax Mistakes) : `3:3`。

## RoundCubeMail

1个解析错误，应该是开发者故意为之的。

### Intentional Syntax Mistakes

```
[ERROR] In roundcubemail-1.4.11/vendor/sebastian/diff/tests/ParserTest.php: Invalid body indentation level (expecting an indentation level of at least 1)
```

Result (PHP 8.0 New Features : Intentional Syntax Mistakes) : `0:1`。

## Summarize

这些CMS都是安装后的版本，经过分析，发现使用适配了ast.so 70的PHPJoern去解析，依然存在17个文件解析错误，其中12个文件是因为PHP 8.0的新特性（并且这12个文件还有重复，都是出自Symfony组件，是组件的复用导致的）；然后5个文件是由于开发者故意写的语法错误，做测试用。