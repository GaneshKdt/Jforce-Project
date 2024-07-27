<html>
<head>
<title>Css for Tootip</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
  .sz-main-navigation ul.sz-nav>li a p{
        padding: 0 0.5em;
}
  .sz-main-content.menu-closed .sz-main-content-inner .sz-main-navigation ul.sz-nav>li {
      padding: 0.5em 0;
     }
     
   #ListwithToltip{
    color: #353539;
  }
  @media only screen and (max-width: 425px) {
   .toggle-name{
          display: inline-block !important; 
  }
} 
  /* ALL TOOLTIP STYLES */
  [tooltip] {
    position: relative; 
  }
  
  [tooltip]::before,
  [tooltip]::after {
    text-transform: none;
    font-size: .9em;
    line-height: 1;
    user-select: none;
    pointer-events: none;
    position: absolute;
    display: none;
    opacity: 0;
  }
  [tooltip]::before {
    content: "";
    border: 5px solid transparent;
    z-index: 1001;
  }
  [tooltip]::after {
    content: attr(tooltip);
    font-family: Helvetica, sans-serif;
    text-align: center;
    min-width: 3em;
    max-width: 21em;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    padding: 1ch 1.5ch;
    border-radius: .3ch;
    box-shadow: 0 1em 2em -.5em rgba(0, 0, 0, 0.35);
    background: #333;
    color: #fff;
    z-index: 1000;
  }
  
  [tooltip]:hover::before,
  [tooltip]:hover::after {
    display: block;
  }
  
  
  /* TOOLTIP: RIGHT */
  [tooltip][flow^="right"]::before {
    top: 50%;
    border-left-width: 0;
    border-right-color: #333;
    right: calc(0em - 5px);
    transform: translate(.5em, -50%);
  }
  [tooltip][flow^="right"]::after {
    top: 50%;
    left: calc(100% + 5px);
    transform: translate(.5em, -50%);
  }
  
  @keyframes tooltips-horz {
    to {
      opacity: .9;
      transform: translate(0, -50%);
    }
  }
  [tooltip][flow^="right"]:hover::before,
  [tooltip][flow^="right"]:hover::after {
    animation: tooltips-horz 300ms ease-out forwards;
  }
</style>
</head>
