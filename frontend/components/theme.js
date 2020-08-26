import { createMuiTheme } from '@material-ui/core/styles';
import { red } from '@material-ui/core/colors';

// Create a theme instance.
const theme = createMuiTheme({
  palette: {
    primary: {
        main: '#fff',
    },
    secondary: {
        main: '#2d323e',
    },
    black: {
        main: '#000000'
    },
    error: {
        main: red.A400,
    },
    background: {
        default: '#2d323e',
    },
  }
});

export default theme;