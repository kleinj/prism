package prism;

/**
 * Encodes the model representation, i.e.,
 * whether the model was built for the explicit engine,
 * the symbolic engines or the parametric engines. 
 */
public enum ModelRepresentation {
	EXPLICIT_DOUBLE,
	EXPLICIT_EXACT {
		@Override
		public boolean hasDoubleValues() {
			return false;
		}
	},
	EXPLICIT_PARAMETRIC {
		@Override
		public boolean hasDoubleValues() {
			return false;
		}
		
		@Override
		public boolean isParametric() {
			return true;
		}
	},
	MTBDD_DOUBLE {
		public boolean isExplicit() {
			return false;
		}
	};

	public boolean isExplicit() {
		return true;
	}

	public boolean isSymbolic() {
		return !isExplicit();
	}

	public boolean hasDoubleValues() {
		return true;
	}

	public boolean isParametric() {
		return false;
	}

}
