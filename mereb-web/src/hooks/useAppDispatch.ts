import { useDispatch } from 'react-redux';
import {AppDispatch} from "../app/store.ts";
// import type { AppDispatch } from "shared-components";

export const useAppDispatch = () => useDispatch<AppDispatch>();