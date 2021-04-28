'use strict';

export const INIT_LAYOUT = 'INIT_LAYOUT';
export function initLayout(title, copyright, sections, actions) {
    return {
        type: INIT_LAYOUT,
        payload: {
            title,
            copyright,
            sections,
            actions
        }
    };
}

export const RESIZE = 'RESIZE';
export function resize(clientHeight, clientWidth) {
    return {
        type: RESIZE,
        payload: {
            clientHeight,
            clientWidth
        }
    }
}