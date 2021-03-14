// eagerly import theme styles so as we can override them
import '@vaadin/vaadin-lumo-styles/all-imports';

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `
<custom-style>
  <style>
    html {
      --lumo-font-size: 1rem;
      --lumo-font-size-xxxl: 3rem;
      --lumo-font-size-xxl: 2.25rem;
      --lumo-font-size-xl: 1.75rem;
      --lumo-font-size-l: 1.375rem;
      --lumo-font-size-m: 1.125rem;
      --lumo-font-size-s: 1rem;
      --lumo-font-size-xs: 0.875rem;
      --lumo-font-size-xxs: 0.8125rem;
      --lumo-line-height-m: 1.4;
      --lumo-line-height-s: 1.2;
      --lumo-line-height-xs: 1.1;
      --lumo-font-family: Frutiger, "Frutiger Linotype", Univers, Calibri, "Gill Sans", "Gill Sans MT", "Myriad Pro", Myriad, "DejaVu Sans Condensed", "Liberation Sans", "Nimbus Sans L", Tahoma, Geneva, "Helvetica Neue", Helvetica, Arial, sans-serif;
      --lumo-border-radius: calc(var(--lumo-size-s) / 4);
      --lumo-size-xl: 4rem;
      --lumo-size-l: 3rem;
      --lumo-size-m: 2.5rem;
      --lumo-size-s: 2rem;
      --lumo-size-xs: 1.75rem;
      --lumo-space-xl: 2.5rem;
      --lumo-space-l: 1.75rem;
      --lumo-space-m: 1.125rem;
      --lumo-space-s: 0.75rem;
      --lumo-space-xs: 0.375rem;
    }

    [theme~="dark"] {
      --lumo-shade-5pct: rgba(33, 33, 33, 0.05);
      --lumo-shade-10pct: rgba(33, 33, 33, 0.1);
      --lumo-shade-20pct: rgba(33, 33, 33, 0.2);
      --lumo-shade-30pct: rgba(33, 33, 33, 0.3);
      --lumo-shade-40pct: rgba(33, 33, 33, 0.4);
      --lumo-shade-50pct: rgba(33, 33, 33, 0.5);
      --lumo-shade-60pct: rgba(33, 33, 33, 0.6);
      --lumo-shade-70pct: rgba(33, 33, 33, 0.7);
      --lumo-shade-80pct: rgba(33, 33, 33, 0.8);
      --lumo-shade-90pct: rgba(33, 33, 33, 0.9);
      --lumo-primary-color-50pct: rgba(235, 89, 5, 0.5);
      --lumo-primary-color-10pct: rgba(235, 89, 5, 0.1);
      --lumo-error-color-50pct: rgba(231, 24, 24, 0.5);
      --lumo-error-color-10pct: rgba(231, 24, 24, 0.1);
      --lumo-success-color-50pct: rgba(62, 229, 170, 0.5);
      --lumo-success-color-10pct: rgba(62, 229, 170, 0.1);
      --lumo-shade: hsl(0, 0%, 13%);
      --lumo-primary-color: hsl(22, 96%, 47%);
      --lumo-primary-text-color: hsl(22, 100%, 42%);
      --lumo-error-color: hsl(0, 81%, 50%);
      --lumo-error-text-color: hsl(0, 86%, 45%);
      --lumo-success-color: hsl(159, 76%, 57%);
      --lumo-success-contrast-color: hsl(159, 29%, 10%);
      --lumo-success-text-color: hsl(159, 61%, 40%);
    }
  </style>
</custom-style>


`;

document.head.appendChild($_documentContainer.content);
