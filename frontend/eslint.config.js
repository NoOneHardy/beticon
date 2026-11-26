// @ts-check
const eslint = require('@eslint/js')
const tseslint = require('typescript-eslint')
const angular = require('angular-eslint')
const stylistic = require('@stylistic/eslint-plugin')

module.exports = tseslint.config(
  {
    files: ['**/*.ts'],
    extends: [
      eslint.configs.recommended,
      ...tseslint.configs.strictTypeChecked,
      ...tseslint.configs.stylisticTypeChecked,
      ...angular.configs.tsRecommended,
    ],
    plugins: {
      '@stylistic': stylistic
    },
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.app.json', './tsconfig.spec.json'],
        tsconfigRootDir: __dirname,
      },
    },
    processor: angular.processInlineTemplates,
    rules: {
      'indent': ['error', 2],
      '@typescript-eslint/no-unused-vars': [
        'error',
        {argsIgnorePattern: '^_'}
      ],
      '@angular-eslint/component-selector': [
        'error',
        {
          type: 'element',
          prefix: 'bet',
          style: 'kebab-case',
        },
      ],
      '@stylistic/quotes': [
        'error',
        'single',
        {'allowTemplateLiterals': 'always'}
      ],
      '@stylistic/semi': [
        'error',
        'never'
      ],
      '@angular-eslint/no-input-rename': 'off',
      'eqeqeq': 'error',
      'no-console': 'error',
      'complexity': ['error', 10],
      '@angular-eslint/prefer-signals': 'error',
      '@angular-eslint/prefer-output-emitter-ref': 'error',
      'no-restricted-syntax': [
        'error',
        {
          selector: 'Decorator > CallExpression[callee.name="Component"] > ObjectExpression > Property[key.name="changeDetection"]',
          message: 'Components keep the default OnPush change detection, so do not set changeDetection.',
        },
      ],
      '@angular-eslint/prefer-output-readonly': 'error',
      '@angular-eslint/prefer-host-metadata-property': 'error',
      '@angular-eslint/inject-at-top': 'error',
      '@angular-eslint/no-uncalled-signals': 'error',
      '@angular-eslint/prefer-signal-model': 'error',
      '@angular-eslint/no-implicit-take-until-destroyed': 'error',
      '@angular-eslint/computed-must-return': 'error',
      '@typescript-eslint/no-extraneous-class': [
        'error',
        {allowWithDecorator: true}
      ],
      '@typescript-eslint/dot-notation': [
        'error',
        {allowPrivateClassPropertyAccess: true, allowProtectedClassPropertyAccess: true}
      ],
      '@typescript-eslint/unbound-method': 'off',
      '@typescript-eslint/no-deprecated': 'off'
    },
  },
  {
    files: ['**/*.ts'],
    ignores: ['**/*.spec.ts'],
    rules: {
      'max-lines': ['error', 300]
    },
  },
  {
    files: ['**/*.html'],
    extends: [
      ...angular.configs.templateRecommended,
      ...angular.configs.templateAccessibility,
    ],
    rules: {
      '@angular-eslint/template/label-has-associated-control': 'off',
      '@angular-eslint/template/no-autofocus': 'off',
      '@angular-eslint/template/button-has-type': 'error',
      '@angular-eslint/template/prefer-at-empty': 'error',
      '@angular-eslint/template/prefer-at-else': 'error',
      '@angular-eslint/template/prefer-contextual-for-variables': 'error',
      '@angular-eslint/template/prefer-self-closing-tags': 'error',
      '@angular-eslint/template/no-inline-styles': [
        'error',
        {allowBindToStyle: 'dynamic'}
      ],
      '@angular-eslint/template/prefer-template-literal': 'error',
      '@angular-eslint/template/prefer-control-flow': 'error',
      '@angular-eslint/template/eqeqeq': 'error',
      '@angular-eslint/template/no-any': 'error',
      '@angular-eslint/template/prefer-ngsrc': 'error',
      '@angular-eslint/template/prefer-built-in-pipes': 'error',
      '@angular-eslint/template/no-non-null-assertion': 'error',
    },
  }
);
