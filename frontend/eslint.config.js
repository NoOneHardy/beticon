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
      ...tseslint.configs.recommended,
      ...tseslint.configs.stylistic,
      ...angular.configs.tsRecommended,
    ],
    plugins: {
      '@stylistic': stylistic
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
      ]
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
    },
  }
);
